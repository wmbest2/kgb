package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory

import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class LR35902LoadETests {

    val memory = UByteArrayMemory(0x0u..0xFFFFu)
    lateinit var cpu: LR35902

    @BeforeTest
    fun setup() {
        cpu = LR35902(memory)
        cpu.programCounter = 0x0u
    }

    @Test
    fun `Load A into E (0x5F)`() {
        cpu.A = 0x23u

        memory.set(0u, 0x5Fu)

        cpu.step()
        assertEquals(cpu.A, cpu.E)
    }

    @Test
    fun `Load B into E (0x58)`() {
        cpu.B = 0x23u

        memory.set(0u, 0x58u)

        cpu.step()
        assertEquals(cpu.B, cpu.E)
    }

    @Test
    fun `Load C into E (0x59)`() {
        cpu.C = 0x23u

        memory.set(0u, 0x59u)

        cpu.step()
        assertEquals(cpu.C, cpu.E)
    }

    @Test
    fun `Load D into E (0x5A)`() {
        cpu.D = 0x23u

        memory.set(0u, 0x5Au)

        cpu.step()
        assertEquals(cpu.D, cpu.E)
    }

    @Test
    fun `Load E into E (0x5B)`() {
        cpu.E = 0x23u

        memory.set(0u, 0x5Bu)

        cpu.step()
        assertEquals(cpu.E, cpu.E)
    }

    @Test
    fun `Load H into E (0x5C)`() {
        cpu.H = 0x23u

        memory.set(0u, 0x5Cu)

        cpu.step()
        assertEquals(cpu.H, cpu.E)
    }

    @Test
    fun `Load L into E (0x5D)`() {
        cpu.L = 0x23u

        memory.set(0u, 0x5Du)

        cpu.step()
        assertEquals(cpu.L, cpu.E)
    }

    @Test
    fun `Load (HL) into E (0x5E)`() {
        cpu.H = 0x01u // 0x01__
        cpu.L = 0x23u // 0x0123

        memory.set(0u, 0x5Eu)
        memory.set(0x123u, 0x18u)

        cpu.step()
        assertEquals(memory.get(0x123u), cpu.E)
    }

}
