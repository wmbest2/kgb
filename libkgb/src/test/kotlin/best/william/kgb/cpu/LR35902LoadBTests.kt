package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalUnsignedTypes
class LR35902LoadBTests {

    val memory = UByteArrayMemory(0x0u..0xFFFFu)
    lateinit var cpu: LR35902

    @Before
    fun setup() {
        cpu = LR35902(memory)
        cpu.programCounter = 0x0u
    }

    @Test
    fun `Load A into B (0x47)`() {
        cpu.A = 0x23u

        memory[0u] = 0x47u

        cpu.step()
        assertEquals(0x23u.toUByte(), cpu.B)
    }

    @Test
    fun `Load B into B (0x40)`() {
        cpu.B = 0x23u

        memory[0u] = 0x40u

        cpu.step()
        assertEquals(0x23u.toUByte(), cpu.B)
    }

    @Test
    fun `Load C into B (0x41)`() {
        cpu.C = 0x23u

        memory[0u] = 0x41u

        cpu.step()
        assertEquals(0x23u.toUByte(), cpu.C)
    }

    @Test
    fun `Load D into B (0x42)`() {
        cpu.D = 0x23u

        memory[0u] = 0x42u

        cpu.step()
        assertEquals(0x23u.toUByte(), cpu.B)
    }

    @Test
    fun `Load E into B (0x43)`() {
        cpu.E = 0x23u

        memory[0u] = 0x43u

        cpu.step()
        assertEquals(0x23u.toUByte(), cpu.B)
    }

    @Test
    fun `Load H into B (0x44)`() {
        cpu.H = 0x23u

        memory[0u] = 0x44u

        cpu.step()
        assertEquals(0x23u.toUByte(), cpu.B)
    }

    @Test
    fun `Load L into B (0x45)`() {
        cpu.L = 0x23u

        memory[0u] = 0x45u

        cpu.step()
        assertEquals(0x23u.toUByte(), cpu.B)
    }

    @Test
    fun `Load (HL) into B (0x46)`() {
        cpu.H = 0x01u // 0x01__
        cpu.L = 0x23u // 0x0123

        memory[0u] = 0x46u
        memory[0x123u] = 0x18u

        cpu.step()
        assertEquals(0x18u.toUByte(), cpu.B)
    }

}
