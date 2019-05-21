package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import kgb.util.invertedMasks
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalUnsignedTypes
class LR35902ResetBitTests {

    val memory = UByteArrayMemory(0xFFFFu)
    lateinit var cpu: LR35902

    @Before
    fun setup() {
        cpu = LR35902(memory)
        cpu.programCounter = 0x0u

        memory[0u] = 0xCBu

        // Give HL an address to use
        cpu.HL = 0x123u
    }

    @Test
    fun `RES 0, A`() {
        cpu.A = 0xFFu

        memory[1u] = 0x87u

        cpu.step()
        assertEquals(UByte.invertedMasks[0], cpu.A)
    }

    @Test
    fun `RES 1, A`() {
        cpu.A = 0xFFu

        memory[1u] = 0x8Fu

        cpu.step()
        assertEquals(UByte.invertedMasks[1], cpu.A)
    }

    @Test
    fun `RES 2, A`() {
        cpu.A = 0xFFu

        memory[1u] = 0x97u

        cpu.step()
        assertEquals(UByte.invertedMasks[2], cpu.A)
    }

    @Test
    fun `RES 3, A`() {
        cpu.A = 0xFFu

        memory[1u] = 0x9Fu

        cpu.step()
        assertEquals(UByte.invertedMasks[3], cpu.A)
    }

    @Test
    fun `RES 4, A`() {
        cpu.A = 0xFFu

        memory[1u] = 0xA7u

        cpu.step()
        assertEquals(UByte.invertedMasks[4], cpu.A)
    }

    @Test
    fun `RES 5, A`() {
        cpu.A = 0xFFu

        memory[1u] = 0xAFu

        cpu.step()
        assertEquals(UByte.invertedMasks[5], cpu.A)
    }

    @Test
    fun `RES 6, A`() {
        cpu.A = 0xFFu

        memory[1u] = 0xB7u

        cpu.step()
        assertEquals(UByte.invertedMasks[6], cpu.A)
    }

    @Test
    fun `RES 7, A`() {
        cpu.A = 0xFFu

        memory[1u] = 0xBFu

        cpu.step()
        assertEquals(UByte.invertedMasks[7], cpu.A)
    }

    @Test
    fun `RES 0, B`() {
        cpu.B = 0xFFu

        memory[1u] = 0x80u

        cpu.step()
        assertEquals(UByte.invertedMasks[0], cpu.B)
    }

    @Test
    fun `RES 1, B`() {
        cpu.B = 0xFFu

        memory[1u] = 0x88u

        cpu.step()
        assertEquals(UByte.invertedMasks[1], cpu.B)
    }

    @Test
    fun `RES 2, B`() {
        cpu.B = 0xFFu

        memory[1u] = 0x90u

        cpu.step()
        assertEquals(UByte.invertedMasks[2], cpu.B)
    }

    @Test
    fun `RES 3, B`() {
        cpu.B = 0xFFu

        memory[1u] = 0x98u

        cpu.step()
        assertEquals(UByte.invertedMasks[3], cpu.B)
    }

    @Test
    fun `RES 4, B`() {
        cpu.B = 0xFFu

        memory[1u] = 0xA0u

        cpu.step()
        assertEquals(UByte.invertedMasks[4], cpu.B)
    }

    @Test
    fun `RES 5, B`() {
        cpu.B = 0xFFu

        memory[1u] = 0xA8u

        cpu.step()
        assertEquals(UByte.invertedMasks[5], cpu.B)
    }

    @Test
    fun `RES 6, B`() {
        cpu.B = 0xFFu

        memory[1u] = 0xB0u

        cpu.step()
        assertEquals(UByte.invertedMasks[6], cpu.B)
    }

    @Test
    fun `RES 7, B`() {
        cpu.B = 0xFFu

        memory[1u] = 0xB8u

        cpu.step()
        assertEquals(UByte.invertedMasks[7], cpu.B)
    }

    @Test
    fun `RES 0, C`() {
        cpu.C = 0xFFu

        memory[1u] = 0x81u

        cpu.step()
        assertEquals(UByte.invertedMasks[0], cpu.C)
    }

    @Test
    fun `RES 1, C`() {
        cpu.C = 0xFFu

        memory[1u] = 0x89u

        cpu.step()
        assertEquals(UByte.invertedMasks[1], cpu.C)
    }

    @Test
    fun `RES 2, C`() {
        cpu.C = 0xFFu

        memory[1u] = 0x91u

        cpu.step()
        assertEquals(UByte.invertedMasks[2], cpu.C)
    }

    @Test
    fun `RES 3, C`() {
        cpu.C = 0xFFu

        memory[1u] = 0x99u

        cpu.step()
        assertEquals(UByte.invertedMasks[3], cpu.C)
    }

    @Test
    fun `RES 4, C`() {
        cpu.C = 0xFFu

        memory[1u] = 0xA1u

        cpu.step()
        assertEquals(UByte.invertedMasks[4], cpu.C)
    }

    @Test
    fun `RES 5, C`() {
        cpu.C = 0xFFu

        memory[1u] = 0xA9u

        cpu.step()
        assertEquals(UByte.invertedMasks[5], cpu.C)
    }

    @Test
    fun `RES 6, C`() {
        cpu.C = 0xFFu

        memory[1u] = 0xB1u

        cpu.step()
        assertEquals(UByte.invertedMasks[6], cpu.C)
    }

    @Test
    fun `RES 7, C`() {
        cpu.C = 0xFFu

        memory[1u] = 0xB9u

        cpu.step()
        assertEquals(UByte.invertedMasks[7], cpu.C)
    }

    @Test
    fun `RES 0, D`() {
        cpu.D = 0xFFu

        memory[1u] = 0x82u

        cpu.step()
        assertEquals(UByte.invertedMasks[0], cpu.D)
    }

    @Test
    fun `RES 1, D`() {
        cpu.D = 0xFFu

        memory[1u] = 0x8Au

        cpu.step()
        assertEquals(UByte.invertedMasks[1], cpu.D)
    }

    @Test
    fun `RES 2, D`() {
        cpu.D = 0xFFu

        memory[1u] = 0x92u

        cpu.step()
        assertEquals(UByte.invertedMasks[2], cpu.D)
    }

    @Test
    fun `RES 3, D`() {
        cpu.D = 0xFFu

        memory[1u] = 0x9Au

        cpu.step()
        assertEquals(UByte.invertedMasks[3], cpu.D)
    }

    @Test
    fun `RES 4, D`() {
        cpu.D = 0xFFu

        memory[1u] = 0xA2u

        cpu.step()
        assertEquals(UByte.invertedMasks[4], cpu.D)
    }

    @Test
    fun `RES 5, D`() {
        cpu.D = 0xFFu

        memory[1u] = 0xAAu

        cpu.step()
        assertEquals(UByte.invertedMasks[5], cpu.D)
    }

    @Test
    fun `RES 6, D`() {
        cpu.D = 0xFFu

        memory[1u] = 0xB2u

        cpu.step()
        assertEquals(UByte.invertedMasks[6], cpu.D)
    }

    @Test
    fun `RES 7, D`() {
        cpu.D = 0xFFu

        memory[1u] = 0xBAu

        cpu.step()
        assertEquals(UByte.invertedMasks[7], cpu.D)
    }


    @Test
    fun `RES 0, E`() {
        cpu.E = 0xFFu

        memory[1u] = 0x83u

        cpu.step()
        assertEquals(UByte.invertedMasks[0], cpu.E)
    }

    @Test
    fun `RES 1, E`() {
        cpu.E = 0xFFu

        memory[1u] = 0x8Bu

        cpu.step()
        assertEquals(UByte.invertedMasks[1], cpu.E)
    }

    @Test
    fun `RES 2, E`() {
        cpu.E = 0xFFu

        memory[1u] = 0x93u

        cpu.step()
        assertEquals(UByte.invertedMasks[2], cpu.E)
    }

    @Test
    fun `RES 3, E`() {
        cpu.E = 0xFFu

        memory[1u] = 0x9Bu

        cpu.step()
        assertEquals(UByte.invertedMasks[3], cpu.E)
    }

    @Test
    fun `RES 4, E`() {
        cpu.E = 0xFFu

        memory[1u] = 0xA3u

        cpu.step()
        assertEquals(UByte.invertedMasks[4], cpu.E)
    }

    @Test
    fun `RES 5, E`() {
        cpu.E = 0xFFu

        memory[1u] = 0xABu

        cpu.step()
        assertEquals(UByte.invertedMasks[5], cpu.E)
    }

    @Test
    fun `RES 6, E`() {
        cpu.E = 0xFFu

        memory[1u] = 0xB3u

        cpu.step()
        assertEquals(UByte.invertedMasks[6], cpu.E)
    }

    @Test
    fun `RES 7, E`() {
        cpu.E = 0xFFu

        memory[1u] = 0xBBu

        cpu.step()
        assertEquals(UByte.invertedMasks[7], cpu.E)
    }

    @Test
    fun `RES 0, H`() {
        cpu.H = 0xFFu

        memory[1u] = 0x84u

        cpu.step()
        assertEquals(UByte.invertedMasks[0], cpu.H)
    }

    @Test
    fun `RES 1, H`() {
        cpu.H = 0xFFu

        memory[1u] = 0x8Cu

        cpu.step()
        assertEquals(UByte.invertedMasks[1], cpu.H)
    }

    @Test
    fun `RES 2, H`() {
        cpu.H = 0xFFu

        memory[1u] = 0x94u

        cpu.step()
        assertEquals(UByte.invertedMasks[2], cpu.H)
    }

    @Test
    fun `RES 3, H`() {
        cpu.H = 0xFFu

        memory[1u] = 0x9Cu

        cpu.step()
        assertEquals(UByte.invertedMasks[3], cpu.H)
    }

    @Test
    fun `RES 4, H`() {
        cpu.H = 0xFFu

        memory[1u] = 0xA4u

        cpu.step()
        assertEquals(UByte.invertedMasks[4], cpu.H)
    }

    @Test
    fun `RES 5, H`() {
        cpu.H = 0xFFu

        memory[1u] = 0xACu

        cpu.step()
        assertEquals(UByte.invertedMasks[5], cpu.H)
    }

    @Test
    fun `RES 6, H`() {
        cpu.H = 0xFFu

        memory[1u] = 0xB4u

        cpu.step()
        assertEquals(UByte.invertedMasks[6], cpu.H)
    }

    @Test
    fun `RES 7, H`() {
        cpu.H = 0xFFu

        memory[1u] = 0xBCu

        cpu.step()
        assertEquals(UByte.invertedMasks[7], cpu.H)
    }

    @Test
    fun `RES 0, L`() {
        cpu.L = 0xFFu

        memory[1u] = 0x85u

        cpu.step()
        assertEquals(UByte.invertedMasks[0], cpu.L)
    }

    @Test
    fun `RES 1, L`() {
        cpu.L = 0xFFu

        memory[1u] = 0x8Du

        cpu.step()
        assertEquals(UByte.invertedMasks[1], cpu.L)
    }

    @Test
    fun `RES 2, L`() {
        cpu.L = 0xFFu

        memory[1u] = 0x95u

        cpu.step()
        assertEquals(UByte.invertedMasks[2], cpu.L)
    }

    @Test
    fun `RES 3, L`() {
        cpu.L = 0xFFu

        memory[1u] = 0x9Du

        cpu.step()
        assertEquals(UByte.invertedMasks[3], cpu.L)
    }

    @Test
    fun `RES 4, L`() {
        cpu.L = 0xFFu

        memory[1u] = 0xA5u

        cpu.step()
        assertEquals(UByte.invertedMasks[4], cpu.L)
    }

    @Test
    fun `RES 5, L`() {
        cpu.L = 0xFFu

        memory[1u] = 0xADu

        cpu.step()
        assertEquals(UByte.invertedMasks[5], cpu.L)
    }

    @Test
    fun `RES 6, L`() {
        cpu.L = 0xFFu

        memory[1u] = 0xB5u

        cpu.step()
        assertEquals(UByte.invertedMasks[6], cpu.L)
    }

    @Test
    fun `RES 7, L`() {
        cpu.L = 0xFFu

        memory[1u] = 0xBDu

        cpu.step()
        assertEquals(UByte.invertedMasks[7], cpu.L)
    }

    @Test
    fun `RES 0, (HL)`() {
        memory[cpu.HL] = 0xFFu

        memory[1u] = 0x86u

        cpu.step()
        assertEquals(UByte.invertedMasks[0], memory[cpu.HL])
    }

    @Test
    fun `RES 1, (HL)`() {
        memory[cpu.HL] = 0xFFu

        memory[1u] = 0x8Eu

        cpu.step()
        assertEquals(UByte.invertedMasks[1], memory[cpu.HL])
    }

    @Test
    fun `RES 2, (HL)`() {
        memory[cpu.HL] = 0xFFu

        memory[1u] = 0x96u

        cpu.step()
        assertEquals(UByte.invertedMasks[2], memory[cpu.HL])
    }

    @Test
    fun `RES 3, (HL)`() {
        memory[cpu.HL] = 0xFFu

        memory[1u] = 0x9Eu

        cpu.step()
        assertEquals(UByte.invertedMasks[3], memory[cpu.HL])
    }

    @Test
    fun `RES 4, (HL)`() {
        memory[cpu.HL] = 0xFFu

        memory[1u] = 0xA6u

        cpu.step()
        assertEquals(UByte.invertedMasks[4], memory[cpu.HL])
    }

    @Test
    fun `RES 5, (HL)`() {
        memory[cpu.HL] = 0xFFu

        memory[1u] = 0xAEu

        cpu.step()
        assertEquals(UByte.invertedMasks[5], memory[cpu.HL])
    }

    @Test
    fun `RES 6, (HL)`() {
        memory[cpu.HL] = 0xFFu

        memory[1u] = 0xB6u

        cpu.step()
        assertEquals(UByte.invertedMasks[6], memory[cpu.HL])
    }

    @Test
    fun `RES 7, (HL)`() {
        memory[cpu.HL] = 0xFFu

        memory[1u] = 0xBEu

        cpu.step()
        assertEquals(UByte.invertedMasks[7], memory[cpu.HL])
    }
}
