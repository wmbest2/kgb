package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory

import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class LR35902LoadHTests {

    val memory = UByteArrayMemory(0x0u..0xFFFFu)
    lateinit var cpu: LR35902

    @BeforeTest
    fun setup() {
        cpu = LR35902(memory)
        cpu.programCounter = 0x0u
    }

    @Test
    fun `Load A into H (0x67)`() {
        cpu.A = 0x23u

        memory.set(0u, 0x67u)

        cpu.step()
        assertEquals(cpu.A, cpu.H)
    }

    @Test
    fun `Load B into H (0x60)`() {
        cpu.B = 0x23u

        memory.set(0u, 0x60u)

        cpu.step()
        assertEquals(cpu.B, cpu.H)
    }

    @Test
    fun `Load C into H (0x61)`() {
        cpu.C = 0x23u

        memory.set(0u, 0x61u)

        cpu.step()
        assertEquals(cpu.C, cpu.H)
    }

    @Test
    fun `Load D into H (0x62)`() {
        cpu.D = 0x23u

        memory.set(0u, 0x62u)

        cpu.step()
        assertEquals(cpu.D, cpu.H)
    }

    @Test
    fun `Load E into H (0x63)`() {
        cpu.E = 0x23u

        memory.set(0u, 0x63u)

        cpu.step()
        assertEquals(cpu.E, cpu.H)
    }

    @Test
    fun `Load H into H (0x64)`() {
        cpu.H = 0x23u

        memory.set(0u, 0x64u)

        cpu.step()
        assertEquals(cpu.H, cpu.H)
    }

    @Test
    fun `Load L into H (0x65)`() {
        cpu.L = 0x23u

        memory.set(0u, 0x65u)

        cpu.step()
        assertEquals(cpu.L, cpu.H)
    }

    @Test
    fun `Load (HL) into H (0x66)`() {
        cpu.H = 0x01u // 0x01__
        cpu.L = 0x23u // 0x0123

        memory.set(0u, 0x66u)
        memory.set(0x123u, 0x18u)

        cpu.step()
        assertEquals(memory.get(0x123u), cpu.H)
    }

}
