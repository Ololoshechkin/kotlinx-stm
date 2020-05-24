package kotlinx.stm

private val stm = DetachedObjectGraphStmImpl()

fun findNativeSTM(): STM = stm