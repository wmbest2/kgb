package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalUnsignedTypes
class LR35902LoadDTests {

    val memory = UByteArrayMemory(0xFFFFu)
    lateinit var cpu: LR35902

    @Before
    fun setup() {
        cpu = LR35902(memory)
        cpu.programCounter = 0x0u
    }

    @Test
    fun `Load A into D (0x57)`() {
        cpu.A = 0x23u

        memory[0u] = 0x57u

        cpu.step()
        assertEquals(cpu.A, cpu.D)
    }

    @Test
    fun `Load B into D (0x50)`() {
        cpu.B = 0x23u

        memory[0u] = 0x50u

        cpu.step()
        assertEquals(cpu.B, cpu.D)
    }

    @Test
    fun `Load C into D (0x51)`() {
        cpu.C = 0x23u

        memory[0u] = 0x51u

        cpu.step()
        assertEquals(cpu.C, cpu.D)
    }

    @Test
    fun `Load D into D (0x52)`() {
        cpu.D = 0x23u

        memory[0u] = 0x52u

        cpu.step()
        assertEquals(cpu.D, cpu.D)
    }

    @Test
    fun `Load E into D (0x53)`() {
        cpu.E = 0x23u

        memory[0u] = 0x53u

        cpu.step()
        assertEquals(cpu.E, cpu.D)
    }

    @Test
    fun `Load H into D (0x54)`() {
        cpu.H = 0x23u

        memory[0u] = 0x54u

        cpu.step()
        assertEquals(cpu.H, cpu.D)
    }

    @Test
    fun `Load L into D (0x55)`() {
        cpu.L = 0x23u

        memory[0u] = 0x55u

        cpu.step()
        assertEquals(cpu.L, cpu.D)
    }

    @Test
    fun `Load (HL) into D (0x56)`() {
        cpu.H = 0x01u // 0x01__
        cpu.L = 0x23u // 0x0123

        memory[0u] = 0x56u
        memory[0x123u] = 0x18u

        cpu.step()
        assertEquals(memory[0x123u], cpu.D)
    }

}
