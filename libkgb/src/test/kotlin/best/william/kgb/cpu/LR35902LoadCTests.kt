package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalUnsignedTypes
class LR35902LoadCTests {

    val memory = UByteArrayMemory(0x0u..0xFFFFu)
    lateinit var cpu: LR35902

    @Before
    fun setup() {
        cpu = LR35902(memory)
        cpu.programCounter = 0x0u
    }

    @Test
    fun `Load A into C (0x4F)`() {
        cpu.A = 0x23u

        memory[0u] = 0x4Fu

        cpu.step()
        assertEquals(cpu.A, cpu.C)
    }

    @Test
    fun `Load B into C (0x48)`() {
        cpu.B = 0x23u

        memory[0u] = 0x48u

        cpu.step()
        assertEquals(cpu.B, cpu.C)
    }

    @Test
    fun `Load C into C (0x49)`() {
        cpu.C = 0x23u

        memory[0u] = 0x49u

        cpu.step()
        assertEquals(cpu.C, cpu.C)
    }

    @Test
    fun `Load D into C (0x4A)`() {
        cpu.D = 0x23u

        memory[0u] = 0x4Au

        cpu.step()
        assertEquals(cpu.D, cpu.C)
    }

    @Test
    fun `Load E into C (0x4B)`() {
        cpu.E = 0x23u

        memory[0u] = 0x4Bu

        cpu.step()
        assertEquals(cpu.E, cpu.C)
    }

    @Test
    fun `Load H into C (0x4C)`() {
        cpu.H = 0x23u

        memory[0u] = 0x4Cu

        cpu.step()
        assertEquals(cpu.H, cpu.C)
    }

    @Test
    fun `Load L into C (0x4D)`() {
        cpu.L = 0x23u

        memory[0u] = 0x4Du

        cpu.step()
        assertEquals(cpu.L, cpu.C)
    }

    @Test
    fun `Load (HL) into C (0x4E)`() {
        cpu.H = 0x01u // 0x01__
        cpu.L = 0x23u // 0x0123

        memory[0u] = 0x4Eu
        memory[0x123u] = 0x18u

        cpu.step()
        assertEquals(memory[0x123u], cpu.C)
    }

}
