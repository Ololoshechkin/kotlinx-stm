@file:Suppress("UNCHECKED_CAST")

package kotlinx.stm

import scala.concurrent.stm.*
import scala.reflect.*
import java.util.concurrent.Callable

internal class JavaSTMContext(var txn: InTxn) : STMContext

internal class JvmDelegate<T> private constructor(
    private val ref: Ref<Any>,
    override val stm: ScalaStmImpl
) : UniversalDelegate<T> {

    companion object {
        fun <T> create(t: T, stm: ScalaStmImpl): JvmDelegate<T> {
            val ref = `Ref$`.`MODULE$`.apply(t, `ManifestFactory$`.`MODULE$`.Object())
            return JvmDelegate(ref, stm)
        }
    }

    override fun unpack(ctx: STMContext): T = ref.get((ctx as JavaSTMContext).txn) as T

    override fun unpackTransactional(ctx: STMContext) = unpack(ctx)

    override fun pack(value: T, ctx: STMContext) {
        ref.set(value, (ctx as JavaSTMContext).txn)
    }

    override fun packTransactional(value: T, ctx: STMContext) = pack(value, ctx)
}

@Suppress("OVERRIDE_BY_INLINE")
class ScalaStmImpl : STM() {
    override fun getContext(): STMContext? {
        val curTxn = Txn.findCurrent(`TxnUnknown$`.`MODULE$`)
        return when {
            curTxn.isDefined -> JavaSTMContext(curTxn.get())
            else -> null
        }
    }

    override fun <T> tryCommitTransaction(
        transactionContext: STMContext?,
        block: STMContext.() -> T
    ): Pair<T?, Boolean> {
        return scala.concurrent.stm.japi.STM.atomic(Callable {
            val ctx: STMContext = getContext() ?: return@Callable Pair(null, false)
            val res = ctx.block()
            Pair(res, true)
        })
    }

    override fun <T> wrap(initValue: T): UniversalDelegate<T> =
        JvmDelegate.create(initValue, this)
}
