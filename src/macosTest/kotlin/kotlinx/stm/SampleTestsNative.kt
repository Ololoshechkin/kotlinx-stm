package kotlinx.stm

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class User(fname: String, lname: String) {
    private val stm = STMSearcher.getSTM()

    private val firstName = stm.wrap(fname)
    private val lastName = stm.wrap(lname)

    fun _get_firstName_Shared(ctx: STMContext) = stm.getVar(ctx, firstName)
    fun _set_firstName_Shared(ctx: STMContext, newValue: String) = stm.setVar(ctx, firstName, newValue)

    fun _get_lastName_Shared(ctx: STMContext) = stm.getVar(ctx, lastName)
    fun _set_larstName_Shared(ctx: STMContext, newValue: String) = stm.setVar(ctx, lastName, newValue)
}

class SampleTestsNative {

    fun a(u: User, ctx: STMContext) = "atomic user is: ${u._get_firstName_Shared(ctx)} ${u._get_lastName_Shared(ctx)}"

    @Test
    fun testG() {
        val u = User("Vadim", "Briliantov")

        val res = runAtomically {
            val tmp = u._get_firstName_Shared(this)
            u._set_firstName_Shared(this, u._get_lastName_Shared(this))
            u._set_larstName_Shared(this, tmp)

            a(u, this)
        }
        assertEquals("atomic user is: Briliantov Vadim", res)
    }
}