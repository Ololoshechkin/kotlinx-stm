package kotlinx.stm

private val stm = ScalaStmImpl()

fun findJavaSTM(): STM = stm