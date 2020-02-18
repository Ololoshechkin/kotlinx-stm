package kotlinx.stm

import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

class JavaSTM : STM() {
    internal val mtx = ReentrantReadWriteLock()

    override fun <T> startTransaction(currentTransactionId: Long?, block: (Long) -> T): Long {
        return 0L
    }

    override fun <T> tryCommitTransaction(currentTransactionId: Long, block: (Long) -> T): Pair<T, Boolean> {
        val res = mtx.write { block(currentTransactionId) }
        return Pair(res, true)
    }

    override fun <T> wrap(initValue: T) = RWLockDelegate(initValue, this)

    class RWLockDelegate<T>(t: T, override val stm: JavaSTM): DummyDelegate<T>(t, stm) {
        private val mtx: ReadWriteLock
            get() = stm.mtx

        override fun pack(value: T) = stm.mtx.write { t = value }

        override fun unpack(): T = stm.mtx.read { t }
    }
}

actual object STMSearcher {
    private val stm = JavaSTM()

    actual fun getSTM(): STM = stm
}
