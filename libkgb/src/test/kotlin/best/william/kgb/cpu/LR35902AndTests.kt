package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalUnsignedTypes
class LR35902AndTests {

    val memory = UByteArrayMemory(0x0u..0xFFFFu)
    lateinit var cpu: LR35902

    @Before
    fun setup() {
        cpu = LR35902(memory)
        cpu.programCounter = 0x0u
        cpu.A = 0x55u
    }

    @Test
    fun `AND A  (0xA7) - All`() {
        cpu.A = 0xFFu

        memory[0u] = 0xA7u

        testAll()
    }

    @Test
    fun `AND A  (0xA7) - None`() {
        cpu.A = 0x0u
        memory[0u] = 0xA7u

        testNone()
    }

    @Test
    fun `AND B  (0xA0) - All`() {
        cpu.A = 0xFFu
        cpu.B = 0xFFu

        memory[0u] = 0xA0u

        testAll()
    }

    @Test
    fun `AND B (0xA0) - None`() {
        cpu.B = 0xAAu
        memory[0u] = 0xA0u

        testNone()
    }

    @Test
    fun `AND C  (0xA1) - All`() {
        cpu.A = 0xFFu
        cpu.C = 0xFFu

        memory[0u] = 0xA1u


        testAll()
    }

    @Test
    fun `AND C (0xA1) - None`() {
        cpu.C = 0xAAu
        memory[0u] = 0xA1u

        testNone()
    }

    @Test
    fun `AND D  (0xA2) - All`() {
        cpu.A = 0xFFu
        cpu.D = 0xFFu

        memory[0u] = 0xA2u


        testAll()
    }

    @Test
    fun `AND D (0xA2) - None`() {
        cpu.D = 0xAAu
        memory[0u] = 0xA2u

        testNone()
    }

    @Test
    fun `AND E  (0xA3) - All`() {
        cpu.A = 0xFFu
        cpu.E = 0xFFu

        memory[0u] = 0xA3u


        testAll()
    }

    @Test
    fun `AND E (0xA3) - None`() {
        cpu.E = 0xAAu
        memory[0u] = 0xA3u

        testNone()
    }

    @Test
    fun `AND H  (0xA4) - All`() {
        cpu.A = 0xFFu
        cpu.H = 0xFFu

        memory[0u] = 0xA4u

        testAll()
    }

    @Test
    fun `AND H (0xA4) - None`() {
        cpu.H = 0xAAu
        memory[0u] = 0xA4u

        testNone()
    }

    @Test
    fun `AND L  (0xA5) - All`() {
        cpu.A = 0xFFu
        cpu.L = 0xFFu

        memory[0u] = 0xA5u
        testAll()
    }

    @Test
    fun `AND L (0xA5) - None`() {
        cpu.L = 0xAAu
        memory[0u] = 0xA5u

        testNone()
    }

    @Test
    fun `AND (HL) (0xA6) - All`() {
        cpu.HL = 0x0123u // 0x01__
        cpu.A = 0xFFu

        memory[0u] = 0xA6u
        memory[0x123u] = 0xFFu
        testAll()
    }

    @Test
    fun `AND (HL) (0xA6) - None`() {
        cpu.HL = 0x0123u
        memory[0u] = 0xA6u
        memory[0x123u] = 0xAAu
        testNone()
    }

    private fun testAll() {
        cpu.step()
        assertEquals(0xFFu.toUByte(), cpu.A)
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.subtractBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)
    }

    private fun testNone() {
        cpu.step()
        assertEquals(0x0.toUByte(), cpu.A)
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.subtractBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)
    }

}
