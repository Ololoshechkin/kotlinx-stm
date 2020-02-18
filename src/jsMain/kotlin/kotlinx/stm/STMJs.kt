package kotlinx.stm

actual object STMSearcher {
    actual fun getSTM() = object : STM() {
        override fun <T> startTransaction(currentTransactionId: Long?, block: (Long) -> T): Long = 0L

        override fun <T> tryCommitTransaction(currentTransactionId: Long, block: (Long) -> T): Pair<T, Boolean> =
            Pair(block(currentTransactionId), true)

        override fun <T> wrap(initValue: T): UniversalDelegate<T> = DummyDelegate(initValue, this)

    }
}