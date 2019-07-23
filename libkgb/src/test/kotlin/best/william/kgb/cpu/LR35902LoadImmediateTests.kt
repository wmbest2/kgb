package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import org.junit.Assert.*
import org.junit.Test

@ExperimentalUnsignedTypes
class LR35902LoadImmediateTests {

    val memory = UByteArrayMemory(0x0u..0xFFFFu)
    val cpu = LR35902(memory)

    @Test
    fun `Verify Load A Immediate (0x06) loads the next byte into Register A`() {
        memory[0u] = 0x3Eu // Load next Byte into B
        memory[1u] = 0x53u

        cpu.programCounter = 0x0u
        cpu.step()

        assertEquals(0x2u.toUShort(), cpu.programCounter)
        assertEquals(0x53u.toUByte(), cpu.A)
    }

    @Test
    fun `Verify Load B Immediate (0x06) loads the next byte into Register B`() {
        memory[0u] = 0x06u // Load next Byte into B
        memory[1u] = 0x53u

        cpu.programCounter = 0x0u
        cpu.step()

        assertEquals(0x2u.toUShort(), cpu.programCounter)
        assertEquals(0x53u.toUByte(), cpu.B)
    }


    @Test
    fun `Verify Load C Immediate (0x0e) loads the next byte into Register C`() {
        memory[0u] = 0x0Eu // Load next Byte into B
        memory[1u] = 0x53u

        cpu.programCounter = 0x0u
        cpu.step()

        assertEquals(0x2u.toUShort(), cpu.programCounter)
        assertEquals(0x53u.toUByte(), cpu.C)
    }


    @Test
    fun `Verify Load D Immediate (0x16) loads the next byte into Register D`() {
        memory[0u] = 0x16u // Load next Byte into B
        memory[1u] = 0x53u

        cpu.programCounter = 0x0u
        cpu.step()

        assertEquals(0x2u.toUShort(), cpu.programCounter)
        assertEquals(0x53u.toUByte(), cpu.D)
    }

    @Test
    fun `Verify Load E Immediate (0x1e) loads the next byte into Register E`() {
        memory[0u] = 0x1Eu // Load next Byte into B
        memory[1u] = 0x53u

        cpu.programCounter = 0x0u
        cpu.step()

        assertEquals(0x2u.toUShort(), cpu.programCounter)
        assertEquals(0x53u.toUByte(), cpu.E)
    }

    @Test
    fun `Verify Load H Immediate (0x26) loads the next byte into Register H`() {
        memory[0u] = 0x26u // Load next Byte into B
        memory[1u] = 0x53u

        cpu.programCounter = 0x0u
        cpu.step()

        assertEquals(0x2u.toUShort(), cpu.programCounter)
        assertEquals(0x53u.toUByte(), cpu.H)
    }

    @Test
    fun `Verify Load L Immediate (0x2e) loads the next byte into Register L`() {
        memory[0u] = 0x2Eu // Load next Byte into B
        memory[1u] = 0x53u

        cpu.programCounter = 0x0u
        cpu.step()

        assertEquals(0x2u.toUShort(), cpu.programCounter)
        assertEquals(0x53u.toUByte(), cpu.L)
    }
}
