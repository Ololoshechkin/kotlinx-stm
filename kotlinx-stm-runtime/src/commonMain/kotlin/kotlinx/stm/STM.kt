package kotlinx.stm

interface STMContext

interface UniversalDelegate<T> {
    val stm: STM

    fun unpack(ctx: STMContext): T

    fun unpackTransactional(ctx: STMContext): T

    fun pack(value: T, ctx: STMContext)

    fun packTransactional(value: T, ctx: STMContext)
}

object DummySTMContext : STMContext

open class DummyDelegate<T>(private var t: T, override val stm: STM) : UniversalDelegate<T> {
    override fun unpack(ctx: STMContext): T = t

    override fun unpackTransactional(ctx: STMContext): T = unpack(ctx)

    override fun pack(value: T, ctx: STMContext) {
        t = value
    }

    override fun packTransactional(value: T, ctx: STMContext) = pack(value, ctx)
}

@Suppress("UNCHECKED_CAST")
abstract class STM {

    protected open fun <T> beforeTransaction(context: STMContext?, block: STMContext.() -> T) {}

    // returns: new transaction id
    protected abstract fun getContext(): STMContext?

    protected abstract fun <T> tryCommitTransaction(
        transactionContext: STMContext?,
        block: STMContext.() -> T
    ): Pair<T?, Boolean>

    fun <T> runAtomically(context: STMContext? = null, block: STMContext.() -> T): T {
        beforeTransaction(context, block)
        while (true) {
            val (res, ok) = tryCommitTransaction(context, block)
            if (ok) return res as T
        }
    }

    abstract fun <T> wrap(initValue: T): UniversalDelegate<T>

    fun <T> getVar(context: STMContext?, delegate: UniversalDelegate<T>): T =
        context
            ?.let(delegate::unpack)
            ?: runAtomically { delegate.unpackTransactional(this) }

    fun <T> setVar(
        context: STMContext? = null,
        delegate: UniversalDelegate<T>,
        newValue: T
    ): Unit =
        context
            ?.let { delegate.packTransactional(newValue, it) }
            ?: runAtomically { delegate.packTransactional(newValue, this) }

}

@Target(AnnotationTarget.CLASS)
annotation class SharedMutable

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.TYPE)
annotation class AtomicFunction

object DummySTM : STM() {
    override fun getContext(): STMContext? = DummySTMContext

    override fun <T> tryCommitTransaction(
        transactionContext: STMContext?,
        block: STMContext.() -> T
    ): Pair<T, Boolean> = Pair(DummySTMContext.block(), true)

    override fun <T> wrap(initValue: T) = DummyDelegate(initValue, this)
}

// Note: functions annotated as @TemporaryIrFunction will be replaced during IR transformation
@Target(AnnotationTarget.FUNCTION)
annotation class ReplaceableAtomicFunction

fun <T> runAtomically(stm: STM, block: STMContext.() -> T): T =
    stm.runAtomically(null, block)

@ReplaceableAtomicFunction
fun <T> runAtomically(block: STMContext.() -> T): T =
    throw IllegalStateException("Method runAtomically must never be called without IR transformation")


/*

class C {
   var x: Int
}

-->

class C {
  private val stm: STM

  private x_delegate: Delegate<Int>

  fun _get_x_shared(ctx: Context) = stm.getVar(ctx, x_delegate)
  fun _set_x_shared(ctx: Context, newValue) { stm.setVar(ctx, x_delegate, newValue) }

}

###################
usage:

val c: C
...

c.x

-->

c._get_x_shared(ctx)

*/