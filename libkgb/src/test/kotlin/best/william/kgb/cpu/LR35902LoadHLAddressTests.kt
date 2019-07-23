package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalUnsignedTypes
class LR35902LoadHLAddressTests {

    val memory = UByteArrayMemory(0x0u..0xFFFFu)
    lateinit var cpu: LR35902

    @Before
    fun setup() {
        cpu = LR35902(memory)
        cpu.programCounter = 0x0u

        cpu.HL = 0x123u
    }

    @Test
    fun `Load A into (HL) (0x77)`() {
        cpu.A = 0x23u

        memory[0u] = 0x77u

        cpu.step()
        assertEquals(cpu.A, memory[cpu.HL])
    }

    @Test
    fun `Load B into (HL) (0x70)`() {
        cpu.B = 0x23u

        memory[0u] = 0x70u

        cpu.step()
        assertEquals(cpu.B, memory[cpu.HL])
    }

    @Test
    fun `Load C into (HL) (0x71)`() {
        cpu.C = 0x23u

        memory[0u] = 0x71u

        cpu.step()
        assertEquals(cpu.C, memory[cpu.HL])
    }

    @Test
    fun `Load D into (HL) (0x72)`() {
        cpu.D = 0x23u

        memory[0u] = 0x72u

        cpu.step()
        assertEquals(cpu.D, memory[cpu.HL])
    }

    @Test
    fun `Load E into (HL) (0x73)`() {
        cpu.E = 0x23u

        memory[0u] = 0x73u

        cpu.step()
        assertEquals(cpu.E, memory[cpu.HL])
    }

    @Test
    fun `Load H into (HL) (0x74)`() {
        memory[0u] = 0x74u

        cpu.step()
        assertEquals(cpu.H, memory[cpu.HL])
    }

    @Test
    fun `Load L into (HL) (0x75)`() {
        memory[0u] = 0x75u

        cpu.step()
        assertEquals(cpu.L, memory[cpu.HL])
    }

}
