package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory

import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class LR35902OrTests {

    val memory = UByteArrayMemory(0x0u..0xFFFFu)
    lateinit var cpu: LR35902

    @BeforeTest
    fun setup() {
        cpu = LR35902(memory)
        cpu.programCounter = 0x0u
        cpu.A = 0x55u
    }

    @Test
    fun `Or A  (0xB7) - All`() {
        cpu.A = 0xFFu.toUByte()

        memory.set(0u, 0xB7u)

        testAll()
    }

    @Test
    fun `Or A  (0xB7) - None`() {
        cpu.A = 0x0u
        memory.set(0u, 0xB7u)

        testNone()
    }

    @Test
    fun `Or B  (0xB0) - All`() {
        cpu.B = 0xAAu

        memory.set(0u, 0xB0u)

        testAll()
    }

    @Test
    fun `Or B (0xB0) - None`() {
        cpu.A = 0x0u
        cpu.B = 0x0u
        memory.set(0u, 0xB0u)

        testNone()
    }

    @Test
    fun `Or C  (0xB1) - All`() {
        cpu.C = 0xAAu

        memory.set(0u, 0xB1u)


        testAll()
    }

    @Test
    fun `Or C (0xB1) - None`() {
        cpu.A = 0x0u
        cpu.C = 0x0u
        memory.set(0u, 0xB1u)

        testNone()
    }

    @Test
    fun `Or D  (0xB2) - All`() {
        cpu.D = 0xAAu

        memory.set(0u, 0xB2u)


        testAll()
    }

    @Test
    fun `Or D (0xB2) - None`() {
        cpu.A = 0x0u
        cpu.D = 0x0u
        memory.set(0u, 0xB2u)

        testNone()
    }

    @Test
    fun `Or E  (0xB3) - All`() {
        cpu.E = 0xAAu

        memory.set(0u, 0xB3u)


        testAll()
    }

    @Test
    fun `Or E (0xB3) - None`() {
        cpu.A = 0x0u
        cpu.E = 0x0u
        memory.set(0u, 0xB3u)

        testNone()
    }

    @Test
    fun `Or H  (0xB4) - All`() {
        cpu.H = 0xAAu

        memory.set(0u, 0xB4u)

        testAll()
    }

    @Test
    fun `Or H (0xB4) - None`() {
        cpu.A = 0x0u
        cpu.H = 0x0u
        memory.set(0u, 0xB4u)

        testNone()
    }

    @Test
    fun `Or L  (0xB5) - All`() {
        cpu.L = 0xAAu

        memory.set(0u, 0xB5u)
        testAll()
    }

    @Test
    fun `Or L (0xB5) - None`() {
        cpu.A = 0x0u
        cpu.L = 0x0u
        memory.set(0u, 0xB5u)

        testNone()
    }

    @Test
    fun `Or (HL) (0xB6) - All`() {
        cpu.HL = 0x0123u // 0x01__

        memory.set(0u, 0xB6u)
        memory.set(0x123u, 0xAAu)
        testAll()
    }

    @Test
    fun `Or (HL) (0xB6) - None`() {
        cpu.A = 0x0u
        cpu.HL = 0x0123u
        memory.set(0u, 0xB6u)
        memory.set(0x123u, 0x0u)
        testNone()
    }

    private fun testAll() {
        cpu.step()
        assertEquals(0xFFu.toUByte(), cpu.A)
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.subtractBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(false, cpu.halfCarryBit)
    }

    private fun testNone() {
        cpu.step()
        assertEquals(0x0.toUByte(), cpu.A)
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.subtractBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(false, cpu.halfCarryBit)
    }

}
