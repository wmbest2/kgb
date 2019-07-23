package best.william.kgb.cpu

import best.william.kgb.test.NanoStopwatch
import kgb.memory.UByteArrayMemory
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.system.measureNanoTime

@ExperimentalUnsignedTypes
class LR35902XorTests {

    @get:Rule
    val stopwatch = NanoStopwatch()

    val memory = UByteArrayMemory(0x0u..0xFFFFu)
    lateinit var cpu: LR35902

    @Before
    fun setup() {
        cpu = LR35902(memory)
        cpu.programCounter = 0x0u
        cpu.A = 0x33u // 0b00110011
                      // 0b00001111 -> 0x0F
                      // ----------
                      // 0b00111100 -> 0x3C
    }

    @Test
    fun `XOR A  (0xAF) `() {

        memory[0u] = 0xAFu

        testNone()
    }

    @Test
    fun `XOR B  (0xA8) `() {
        cpu.B = 0x0Fu

        memory[0u] = 0xA8u

        testXor()
    }

    @Test
    fun `XOR C  (0xA9) `() {
        cpu.C = 0x0Fu

        memory[0u] = 0xA9u


        testXor()
    }

    @Test
    fun `XOR D  (0xAA) `() {
        cpu.D = 0x0Fu

        memory[0u] = 0xAAu


        testXor()
    }

    @Test
    fun `XOR E  (0xAB) `() {
        cpu.E = 0x0Fu

        memory[0u] = 0xABu


        testXor()
    }

    @Test
    fun `XOR H  (0xAC) `() {
        cpu.H = 0x0Fu

        memory[0u] = 0xACu

        testXor()
    }

    @Test
    fun `XOR L  (0xAD) `() {
        cpu.L = 0x0Fu

        memory[0u] = 0xADu
        testXor()
    }

    @Test
    fun `XOR (HL) (0xAE)`() {
        cpu.HL = 0x0123u // 0x01__

        memory[0u] = 0xAEu
        memory[0x123u] = 0x0Fu
        testXor()
    }


    private fun testXor() {
        val time = measureNanoTime {
            cpu.step()
        }
        println("Time for opcode $time")
        assertEquals(0x3Cu.toUByte(), cpu.A)
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
