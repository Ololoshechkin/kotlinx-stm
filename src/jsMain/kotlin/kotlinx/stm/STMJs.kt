package kotlinx.stm

actual object STMSearcher {
    actual fun getSTM() = object : STM() {
        override fun getContext(): STMContext? = DummySTMContext

        override fun <T> tryCommitTransaction(
            transactionContext: STMContext?,
            block: STMContext.() -> T
        ): Pair<T, Boolean> = Pair(DummySTMContext.block(), true)

        override fun <T> wrap(initValue: T) = DummyDelegate(initValue, this)
    }
}