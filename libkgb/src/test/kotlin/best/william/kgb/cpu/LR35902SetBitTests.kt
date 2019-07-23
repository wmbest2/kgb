package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import kgb.util.masks
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalUnsignedTypes
class LR35902SetBitTests {

    val memory = UByteArrayMemory(0x0u..0xFFFFu)
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
    fun `SET 0, A`() {
        cpu.A = 0x0u

        memory[1u] = 0xC7u

        cpu.step()
        assertEquals(UByte.masks[0], cpu.A)
    }

    @Test
    fun `SET 1, A`() {
        cpu.A = 0x0u

        memory[1u] = 0xCFu

        cpu.step()
        assertEquals(UByte.masks[1], cpu.A)
    }

    @Test
    fun `SET 2, A`() {
        cpu.A = 0x0u

        memory[1u] = 0xD7u

        cpu.step()
        assertEquals(UByte.masks[2], cpu.A)
    }

    @Test
    fun `SET 3, A`() {
        cpu.A = 0x0u

        memory[1u] = 0xDFu

        cpu.step()
        assertEquals(UByte.masks[3], cpu.A)
    }

    @Test
    fun `SET 4, A`() {
        cpu.A = 0x0u

        memory[1u] = 0xE7u

        cpu.step()
        assertEquals(UByte.masks[4], cpu.A)
    }

    @Test
    fun `SET 5, A`() {
        cpu.A = 0x0u

        memory[1u] = 0xEFu

        cpu.step()
        assertEquals(UByte.masks[5], cpu.A)
    }

    @Test
    fun `SET 6, A`() {
        cpu.A = 0x0u

        memory[1u] = 0xF7u

        cpu.step()
        assertEquals(UByte.masks[6], cpu.A)
    }

    @Test
    fun `SET 7, A`() {
        cpu.A = 0x0u

        memory[1u] = 0xFFu

        cpu.step()
        assertEquals(UByte.masks[7], cpu.A)
    }

    @Test
    fun `SET 0, B`() {
        cpu.B = 0x0u

        memory[1u] = 0xC0u

        cpu.step()
        assertEquals(UByte.masks[0], cpu.B)
    }

    @Test
    fun `SET 1, B`() {
        cpu.B = 0x0u

        memory[1u] = 0xC8u

        cpu.step()
        assertEquals(UByte.masks[1], cpu.B)
    }

    @Test
    fun `SET 2, B`() {
        cpu.B = 0x0u

        memory[1u] = 0xD0u

        cpu.step()
        assertEquals(UByte.masks[2], cpu.B)
    }

    @Test
    fun `SET 3, B`() {
        cpu.B = 0x0u

        memory[1u] = 0xD8u

        cpu.step()
        assertEquals(UByte.masks[3], cpu.B)
    }

    @Test
    fun `SET 4, B`() {
        cpu.B = 0x0u

        memory[1u] = 0xE0u

        cpu.step()
        assertEquals(UByte.masks[4], cpu.B)
    }

    @Test
    fun `SET 5, B`() {
        cpu.B = 0x0u

        memory[1u] = 0xE8u

        cpu.step()
        assertEquals(UByte.masks[5], cpu.B)
    }

    @Test
    fun `SET 6, B`() {
        cpu.B = 0x0u

        memory[1u] = 0xF0u

        cpu.step()
        assertEquals(UByte.masks[6], cpu.B)
    }

    @Test
    fun `SET 7, B`() {
        cpu.B = 0x0u

        memory[1u] = 0xF8u

        cpu.step()
        assertEquals(UByte.masks[7], cpu.B)
    }

    @Test
    fun `SET 0, C`() {
        cpu.C = 0x0u

        memory[1u] = 0xC1u

        cpu.step()
        assertEquals(UByte.masks[0], cpu.C)
    }

    @Test
    fun `SET 1, C`() {
        cpu.C = 0x0u

        memory[1u] = 0xC9u

        cpu.step()
        assertEquals(UByte.masks[1], cpu.C)
    }

    @Test
    fun `SET 2, C`() {
        cpu.C = 0x0u

        memory[1u] = 0xD1u

        cpu.step()
        assertEquals(UByte.masks[2], cpu.C)
    }

    @Test
    fun `SET 3, C`() {
        cpu.C = 0x0u

        memory[1u] = 0xD9u

        cpu.step()
        assertEquals(UByte.masks[3], cpu.C)
    }

    @Test
    fun `SET 4, C`() {
        cpu.C = 0x0u

        memory[1u] = 0xE1u

        cpu.step()
        assertEquals(UByte.masks[4], cpu.C)
    }

    @Test
    fun `SET 5, C`() {
        cpu.C = 0x0u

        memory[1u] = 0xE9u

        cpu.step()
        assertEquals(UByte.masks[5], cpu.C)
    }

    @Test
    fun `SET 6, C`() {
        cpu.C = 0x0u

        memory[1u] = 0xF1u

        cpu.step()
        assertEquals(UByte.masks[6], cpu.C)
    }

    @Test
    fun `SET 7, C`() {
        cpu.C = 0x0u

        memory[1u] = 0xF9u

        cpu.step()
        assertEquals(UByte.masks[7], cpu.C)
    }

    @Test
    fun `SET 0, D`() {
        cpu.D = 0x0u

        memory[1u] = 0xC2u

        cpu.step()
        assertEquals(UByte.masks[0], cpu.D)
    }

    @Test
    fun `SET 1, D`() {
        cpu.D = 0x0u

        memory[1u] = 0xCAu

        cpu.step()
        assertEquals(UByte.masks[1], cpu.D)
    }

    @Test
    fun `SET 2, D`() {
        cpu.D = 0x0u

        memory[1u] = 0xD2u

        cpu.step()
        assertEquals(UByte.masks[2], cpu.D)
    }

    @Test
    fun `SET 3, D`() {
        cpu.D = 0x0u

        memory[1u] = 0xDAu

        cpu.step()
        assertEquals(UByte.masks[3], cpu.D)
    }

    @Test
    fun `SET 4, D`() {
        cpu.D = 0x0u

        memory[1u] = 0xE2u

        cpu.step()
        assertEquals(UByte.masks[4], cpu.D)
    }

    @Test
    fun `SET 5, D`() {
        cpu.D = 0x0u

        memory[1u] = 0xEAu

        cpu.step()
        assertEquals(UByte.masks[5], cpu.D)
    }

    @Test
    fun `SET 6, D`() {
        cpu.D = 0x0u

        memory[1u] = 0xF2u

        cpu.step()
        assertEquals(UByte.masks[6], cpu.D)
    }

    @Test
    fun `SET 7, D`() {
        cpu.D = 0x0u

        memory[1u] = 0xFAu

        cpu.step()
        assertEquals(UByte.masks[7], cpu.D)
    }


    @Test
    fun `SET 0, E`() {
        cpu.E = 0x0u

        memory[1u] = 0xC3u

        cpu.step()
        assertEquals(UByte.masks[0], cpu.E)
    }

    @Test
    fun `SET 1, E`() {
        cpu.E = 0x0u

        memory[1u] = 0xCBu

        cpu.step()
        assertEquals(UByte.masks[1], cpu.E)
    }

    @Test
    fun `SET 2, E`() {
        cpu.E = 0x0u

        memory[1u] = 0xD3u

        cpu.step()
        assertEquals(UByte.masks[2], cpu.E)
    }

    @Test
    fun `SET 3, E`() {
        cpu.E = 0x0u

        memory[1u] = 0xDBu

        cpu.step()
        assertEquals(UByte.masks[3], cpu.E)
    }

    @Test
    fun `SET 4, E`() {
        cpu.E = 0x0u

        memory[1u] = 0xE3u

        cpu.step()
        assertEquals(UByte.masks[4], cpu.E)
    }

    @Test
    fun `SET 5, E`() {
        cpu.E = 0x0u

        memory[1u] = 0xEBu

        cpu.step()
        assertEquals(UByte.masks[5], cpu.E)
    }

    @Test
    fun `SET 6, E`() {
        cpu.E = 0x0u

        memory[1u] = 0xF3u

        cpu.step()
        assertEquals(UByte.masks[6], cpu.E)
    }

    @Test
    fun `SET 7, E`() {
        cpu.E = 0x0u

        memory[1u] = 0xFBu

        cpu.step()
        assertEquals(UByte.masks[7], cpu.E)
    }

    @Test
    fun `SET 0, H`() {
        cpu.H = 0x0u

        memory[1u] = 0xC4u

        cpu.step()
        assertEquals(UByte.masks[0], cpu.H)
    }

    @Test
    fun `SET 1, H`() {
        cpu.H = 0x0u

        memory[1u] = 0xCCu

        cpu.step()
        assertEquals(UByte.masks[1], cpu.H)
    }

    @Test
    fun `SET 2, H`() {
        cpu.H = 0x0u

        memory[1u] = 0xD4u

        cpu.step()
        assertEquals(UByte.masks[2], cpu.H)
    }

    @Test
    fun `SET 3, H`() {
        cpu.H = 0x0u

        memory[1u] = 0xDCu

        cpu.step()
        assertEquals(UByte.masks[3], cpu.H)
    }

    @Test
    fun `SET 4, H`() {
        cpu.H = 0x0u

        memory[1u] = 0xE4u

        cpu.step()
        assertEquals(UByte.masks[4], cpu.H)
    }

    @Test
    fun `SET 5, H`() {
        cpu.H = 0x0u

        memory[1u] = 0xECu

        cpu.step()
        assertEquals(UByte.masks[5], cpu.H)
    }

    @Test
    fun `SET 6, H`() {
        cpu.H = 0x0u

        memory[1u] = 0xF4u

        cpu.step()
        assertEquals(UByte.masks[6], cpu.H)
    }

    @Test
    fun `SET 7, H`() {
        cpu.H = 0x0u

        memory[1u] = 0xFCu

        cpu.step()
        assertEquals(UByte.masks[7], cpu.H)
    }

    @Test
    fun `SET 0, L`() {
        cpu.L = 0x0u

        memory[1u] = 0xC5u

        cpu.step()
        assertEquals(UByte.masks[0], cpu.L)
    }

    @Test
    fun `SET 1, L`() {
        cpu.L = 0x0u

        memory[1u] = 0xCDu

        cpu.step()
        assertEquals(UByte.masks[1], cpu.L)
    }

    @Test
    fun `SET 2, L`() {
        cpu.L = 0x0u

        memory[1u] = 0xD5u

        cpu.step()
        assertEquals(UByte.masks[2], cpu.L)
    }

    @Test
    fun `SET 3, L`() {
        cpu.L = 0x0u

        memory[1u] = 0xDDu

        cpu.step()
        assertEquals(UByte.masks[3], cpu.L)
    }

    @Test
    fun `SET 4, L`() {
        cpu.L = 0x0u

        memory[1u] = 0xE5u

        cpu.step()
        assertEquals(UByte.masks[4], cpu.L)
    }

    @Test
    fun `SET 5, L`() {
        cpu.L = 0x0u

        memory[1u] = 0xEDu

        cpu.step()
        assertEquals(UByte.masks[5], cpu.L)
    }

    @Test
    fun `SET 6, L`() {
        cpu.L = 0x0u

        memory[1u] = 0xF5u

        cpu.step()
        assertEquals(UByte.masks[6], cpu.L)
    }

    @Test
    fun `SET 7, L`() {
        cpu.L = 0x0u

        memory[1u] = 0xFDu

        cpu.step()
        assertEquals(UByte.masks[7], cpu.L)
    }

    @Test
    fun `SET 0, (HL)`() {
        memory[cpu.HL] = 0x0u

        memory[1u] = 0xC6u

        cpu.step()
        assertEquals(UByte.masks[0], memory[cpu.HL])
    }

    @Test
    fun `SET 1, (HL)`() {
        memory[cpu.HL] = 0x0u

        memory[1u] = 0xCEu

        cpu.step()
        assertEquals(UByte.masks[1], memory[cpu.HL])
    }

    @Test
    fun `SET 2, (HL)`() {
        memory[cpu.HL] = 0x0u

        memory[1u] = 0xD6u

        cpu.step()
        assertEquals(UByte.masks[2], memory[cpu.HL])
    }

    @Test
    fun `SET 3, (HL)`() {
        memory[cpu.HL] = 0x0u

        memory[1u] = 0xDEu

        cpu.step()
        assertEquals(UByte.masks[3], memory[cpu.HL])
    }

    @Test
    fun `SET 4, (HL)`() {
        memory[cpu.HL] = 0x0u

        memory[1u] = 0xE6u

        cpu.step()
        assertEquals(UByte.masks[4], memory[cpu.HL])
    }

    @Test
    fun `SET 5, (HL)`() {
        memory[cpu.HL] = 0x0u

        memory[1u] = 0xEEu

        cpu.step()
        assertEquals(UByte.masks[5], memory[cpu.HL])
    }

    @Test
    fun `SET 6, (HL)`() {
        memory[cpu.HL] = 0x0u

        memory[1u] = 0xF6u

        cpu.step()
        assertEquals(UByte.masks[6], memory[cpu.HL])
    }

    @Test
    fun `SET 7, (HL)`() {
        memory[cpu.HL] = 0x0u

        memory[1u] = 0xFEu

        cpu.step()
        assertEquals(UByte.masks[7], memory[cpu.HL])
    }
}
