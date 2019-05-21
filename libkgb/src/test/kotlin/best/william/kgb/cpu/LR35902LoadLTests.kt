package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalUnsignedTypes
class LR35902LoadLTests {

    val memory = UByteArrayMemory(0xFFFFu)
    lateinit var cpu: LR35902

    @Before
    fun setup() {
        cpu = LR35902(memory)
        cpu.programCounter = 0x0u
    }

    @Test
    fun `Load A into L (0x6F)`() {
        cpu.A = 0x23u

        memory[0u] = 0x6Fu

        cpu.step()
        assertEquals(cpu.A, cpu.L)
    }

    @Test
    fun `Load B into L (0x68)`() {
        cpu.B = 0x23u

        memory[0u] = 0x68u

        cpu.step()
        assertEquals(cpu.B, cpu.L)
    }

    @Test
    fun `Load C into L (0x69)`() {
        cpu.C = 0x23u

        memory[0u] = 0x69u

        cpu.step()
        assertEquals(cpu.C, cpu.L)
    }

    @Test
    fun `Load D into L (0x6A)`() {
        cpu.D = 0x23u

        memory[0u] = 0x6Au

        cpu.step()
        assertEquals(cpu.D, cpu.L)
    }

    @Test
    fun `Load E into L (0x6B)`() {
        cpu.E = 0x23u

        memory[0u] = 0x6Bu

        cpu.step()
        assertEquals(cpu.E, cpu.L)
    }

    @Test
    fun `Load H into L (0x6C)`() {
        cpu.H = 0x23u

        memory[0u] = 0x6Cu

        cpu.step()
        assertEquals(cpu.H, cpu.L)
    }

    @Test
    fun `Load L into L (0x6D)`() {
        cpu.L = 0x23u

        memory[0u] = 0x6Du

        cpu.step()
        assertEquals(cpu.L, cpu.L)
    }

    @Test
    fun `Load (HL) into L (0x6E)`() {
        cpu.H = 0x01u // 0x01__
        cpu.L = 0x23u // 0x0123

        memory[0u] = 0x6Eu
        memory[0x123u] = 0x18u

        cpu.step()
        assertEquals(memory[0x123u], cpu.L)
    }

}
