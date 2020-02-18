package kotlinx.stm

import kotlin.native.concurrent.*
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.*

typealias Delegate = NativeSTM.NativeDelegate<*>

class NativeSTM : STM() {
    private val mtx = ReentrantLock()

    private val transactionCounter = atomic(0)

    private val unpackedDelegates = atomic(arrayOf<Delegate>())

    // updates unpackedDelegates[] and returns it's old value
    private fun updateDelegates(upd: (Array<Delegate>) -> Array<Delegate>): Array<Delegate> {
        while (true) {
            val delegates = unpackedDelegates.value
            val newDelegates = upd(delegates)
            if (unpackedDelegates.compareAndSet(delegates, newDelegates)) return delegates
        }
    }

    override fun <T> startTransaction(currentTransactionId: Long?, block: (Long) -> T): Long {
        if (!block.isFrozen) block.freeze()
        mtx.lock()
        transactionCounter.incrementAndGet()
        return 0L
    }

    override fun <T> tryCommitTransaction(currentTransactionId: Long, block: (Long) -> T): Pair<T, Boolean> {
        val res = block(currentTransactionId)
        if (!res.isFrozen) res.freeze()

        if (transactionCounter.decrementAndGet() == 0) {
            val delegates = updateDelegates { arrayOf() }

            delegates.forEach {
                it.forcePack(it.cache.value)
            }
        }

        mtx.unlock()
        return Pair(res, true)
    }

    override fun <T> wrap(initValue: T): UniversalDelegate<T> = NativeDelegate(initValue)

    @Suppress("UNCHECKED_CAST")
    class NativeDelegate<T>(override val stm: NativeSTM) : UniversalDelegate<T> {

        data class Cache<T>(val value: T)

        private val delegate: AtomicRef<DetachedObjectGraph<Any?>?> = atomic(null)

        val cache: AtomicRef<Cache<T>?> = atomic(null)

        private val isCached
            get() = cache.value != null

        constructor(initValue: T, stm: NativeSTM = STMSearcher.getSTM() as NativeSTM) : this(stm) {
            initValue.freeze()
            delegate.compareAndSet(null, DetachedObjectGraph { initValue as Any? })
            this.freeze()
        }

        private fun updateCache(newCache: Cache<T>?) {
            while (true) {
                val cachedValue = cache.value
                if (cache.compareAndSet(cachedValue, newCache)) return
            }
        }

        private fun putToCache(newValue: T) = updateCache(Cache(newValue))

        private fun attachDelegate(): T {
            while (true) {
                val curDelegate = delegate.value
                if (curDelegate != null && delegate.compareAndSet(curDelegate, null)) {
                    return curDelegate.attach() as T
                }
            }
        }

        private fun changeDelegate(newValue: T) {
            val newDelegate = DetachedObjectGraph { newValue as Any? }
            while (true) {
                val curDelegate = delegate.value
                if (delegate.compareAndSet(curDelegate, newDelegate)) break
            }
        }

        override fun unpack(): T {
            val cached = cache.value
            if (cached == null) {
                val curValue = attachDelegate()
                putToCache(curValue)
                return curValue
            } else {
                return cached.value
            }
        }

        override fun pack(value: T) =
            if (isCached)
                putToCache(value)
            else
                changeDelegate(value)

        fun forcePack(value: Any?) {
            updateCache(null)
            changeDelegate(value as? T ?: throw IllegalArgumentException("Value $value must be of type T"))
        }

        override fun unpackTransactional(): T {
            if (!isCached) {
                stm.updateDelegates { delegates ->
                    Array(delegates.size + 1) { i ->
                        if (i < delegates.size) delegates[i] else this
                    }
                }
            }

            return unpack()
        }

        override fun packTransactional(value: T) = pack(value)
    }
}

actual object STMSearcher {
    private val stm = NativeSTM()

    actual fun getSTM(): STM = stm
}