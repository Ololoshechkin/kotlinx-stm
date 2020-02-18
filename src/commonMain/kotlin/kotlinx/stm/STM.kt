package kotlinx.stm

interface UniversalDelegate<T> {
    val stm: STM

    fun unpack(): T

    fun unpackTransactional(): T

    fun pack(value: T)

    fun packTransactional(value: T)
}

open class DummyDelegate<T>(protected var t: T, override val stm: STM) : UniversalDelegate<T> {

    override fun unpack(): T = t

    override fun unpackTransactional(): T = unpack()

    override fun pack(value: T) {
        t = value
    }

    override fun packTransactional(value: T) = pack(value)

}

abstract class STM {
    // returns: new transaction id
    protected abstract fun <T> startTransaction(currentTransactionId: Long? = null, block: (Long) -> T): Long

    protected abstract fun <T> tryCommitTransaction(currentTransactionId: Long, block: (Long) -> T): Pair<T, Boolean>

    fun <T> runAtomically(currentTransactionId: Long? = null, block: (Long) -> T): T {
        val newTransactionId = startTransaction(currentTransactionId, block)
        while (true) {
            val (res, ok) = tryCommitTransaction(newTransactionId, block)
            if (ok) return res
        }
    }

    abstract fun <T> wrap(initValue: T): UniversalDelegate<T>

    fun <T> getVar(currentTransactionId: Long? = null, delegate: UniversalDelegate<T>): T =
        if (currentTransactionId == null)
            runAtomically(currentTransactionId) { delegate.unpackTransactional() }
        else
            delegate.unpack()

    fun <T> setVar(
        currentTransactionId: Long? = null,
        delegate: UniversalDelegate<T>,
        newValue: T
    ): Unit =
        if (currentTransactionId == null)
            runAtomically(currentTransactionId) {
                delegate.packTransactional(newValue)
            }
        else
            delegate.pack(newValue)

}

annotation class SharedMutable

expect object STMSearcher {
    fun getSTM(): STM
}

fun <T> runAtomically(stm: STM = STMSearcher.getSTM(), block: () -> T) = stm.runAtomically(null) {
    block()
}