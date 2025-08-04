package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class LR35902CommandTests {

    @Test
    fun `NoOp just increments the counter`() {

        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.programCounter = 0x0u
        cpu.step()

        assertEquals(0x1u.toUShort(), cpu.programCounter)
    }

    @Test
    fun `Halt (0x76) should stop operations until interrupt occurs`() {
        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        memory[0x1u] = 0x76u

        cpu.programCounter = 0x0u

        cpu.step()
        assertEquals(0x1u.toUShort(), cpu.programCounter)

        cpu.step()
        assertEquals(0x2u.toUShort(), cpu.programCounter)

        cpu.step()
        assertEquals(0x2u.toUShort(), cpu.programCounter)
    }

    @Test
    fun `JR NZ, r8 should decrement the PC if not Z`() {

        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.programCounter = 0xAu
        cpu.zeroBit = false

        memory[0xAu] = 0x20u
        memory[0xBu] = (-3).toUByte()

        cpu.step()

        assertEquals(0x9u.toUShort(), cpu.programCounter)

    }

    @Test
    fun `JR NZ, r8 should increment the PC if not Z`() {

        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.programCounter = 0xAu
        cpu.zeroBit = false

        memory.set(0xAu, 0x20u)
        memory.set(0xBu, 0x03u)

        cpu.step()

        assertEquals(0xFu.toUShort(), cpu.programCounter)

    }

    @Test
    fun `JR NZ, r8 should do nothing the PC if Z`() {

        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.programCounter = 0xAu
        cpu.zeroBit = true

        memory.set(0xAu, 0x20u)
        memory.set(0xBu, (-3).toUByte())

        cpu.step()

        assertEquals(0xCu.toUShort(), cpu.programCounter)

    }

    @Test
    fun `JR NZ, r8 should increment the PC if Z`() {

        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.programCounter = 0xAu
        cpu.zeroBit = true

        memory.set(0xAu, 0x20u)
        memory.set(0xBu, 0x03u)

        cpu.step()

        assertEquals(0xCu.toUShort(), cpu.programCounter)

    }

    @Test
    fun `JR negative test`() {
        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.programCounter = 0x10u
        memory.set(0x10u, 0x18u) // JR opcode
        memory.set(0x11u, (-2).toUByte()) // Relative offset -2

        cpu.step()

        assertEquals(0x10u.toUShort(), cpu.programCounter)
    }

    @Test
    fun `JR positive test`() {
        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.programCounter = 0x10u
        memory.set(0x10u, 0x18u) // JR opcode
        memory.set(0x11u, 0x02u) // Relative offset +2

        cpu.step()

        assertEquals(0x14u.toUShort(), cpu.programCounter)
    }

    @Test
    fun `LD PC, HL test`() {
        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.programCounter = 0x10u
        cpu.HL = 0x20u
        memory.set(0x10u, 0xE9u) // LD PC, HL opcode

        cpu.step()

        assertEquals(0x20u.toUShort(), cpu.programCounter)
    }

    @Test
    fun `POP AF test`() {
        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.stackPointer = 0xFFu // Stack pointer before PUSH
        memory.set(0xFEu, 0x34u) // Push BC (high byte)
        memory.set(0xFDu, 0x12u) // Push BC (low byte)
        cpu.stackPointer = 0xFDu // Stack pointer after PUSH

        memory.set(0x10u, 0xF1u) // POP AF opcode
        cpu.programCounter = 0x10u

        cpu.step()

        assertEquals(0x3410u.toUShort().toHexString(), cpu.AF.toHexString())
    }

    @Test
    fun `DAA Addition test`() {
        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.A = 0x15u
        memory.set(0x0Eu, 0xC6u) // ADD A, 0x27 opcode
        memory.set(0x0Fu, 0x27u) // Immediate value 0x27
        memory.set(0x10u, 0x27u) // DAA opcode
        cpu.programCounter = 0xEu

        cpu.step() // Execute ADD A, 0x27

        assertEquals(0x3Cu.toUByte(), cpu.A) // Result of ADD A, 0x27

        cpu.step() // Execute DAA

        assertEquals(0x42u.toUByte(), cpu.A) // Adjusted value
    }

    @Test
    fun `DAA Subtraction test`() {
        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)
        cpu.A = 0x42u
        memory.set(0x0Eu, 0xD6u) // SUB A, 0x27 opcode
        memory.set(0x0Fu, 0x27u) // Immediate value
        memory.set(0x10u, 0x27u) // DAA opcode
        cpu.programCounter = 0xEu

        cpu.step() // Execute SUB A, 0x27
        assertEquals(0x1Bu.toUByte(), cpu.A) // Result of SUB A, 0x27
        cpu.step() // Execute DAA
        assertEquals(0x15u.toUByte(), cpu.A) // Adjusted value
    }

    @Test
    fun `DAA Addition with carry test`() {
        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.A = 0x29u
        memory.set(0x0Eu, 0xC6u) // ADD A, 0x20 opcode
        memory.set(0x0Fu, 0x17u) // Immediate value 0x20
        memory.set(0x10u, 0x27u) // DAA opcode
        cpu.programCounter = 0xEu

        cpu.step() // Execute ADD A, 0x20

        assertEquals(0x40u.toUByte(), cpu.A) // Result of ADD A, 0x20

        cpu.step() // Execute DAA

        assertEquals(0x46u.toUByte(), cpu.A) // Adjusted value remains the same
    }

    @Test
    fun `DAA Addition with carry test 2`() {
        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.A = 0x99u
        memory.set(0x0Eu, 0xC6u) // ADD A, 0x20 opcode
        memory.set(0x0Fu, 0x01u) // Immediate value 0x20
        memory.set(0x10u, 0x27u) // DAA opcode
        cpu.programCounter = 0xEu

        cpu.step() // Execute ADD A, 0x20

        assertEquals(0x9Au.toUByte(), cpu.A) // Result of ADD A, 0x20

        cpu.step() // Execute DAA

        assertEquals(0x00u.toUByte(), cpu.A) // Adjusted value remains the same
    }

    @Test
    fun `DAA Addition overflow test`() {
        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.A = 0x99u
        memory.set(0x0Eu, 0xC6u) // ADD A, 0x99 opcode
        memory.set(0x0Fu, 0x99u) // Immediate value 0x99
        memory.set(0x10u, 0x27u) // DAA opcode
        cpu.programCounter = 0xEu

        cpu.step() // Execute ADD A, 0x99
        assertEquals(0x32u.toUByte(), cpu.A) // Result of ADD A, 0x99 (overflow, wraps to 0x32)
        assertEquals(cpu.carryBit, true)

        cpu.step() // Execute DAA
        assertEquals(0x98u.toUByte(), cpu.A) // Adjusted value
        assertEquals(cpu.carryBit, true)
        assertEquals(cpu.halfCarryBit, false)
    }

    @Test
    fun `DAA Subtraction with half-carry test`() {
        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.A = 0x10u
        memory.set(0x0Eu, 0xD6u) // SUB A, 0x01 opcode
        memory.set(0x0Fu, 0x01u) // Immediate value
        memory.set(0x10u, 0x27u) // DAA opcode
        cpu.programCounter = 0xEu

        cpu.step() // Execute SUB A, 0x01
        assertEquals(0x0Fu.toUByte(), cpu.A) // Result of SUB A, 0x01

        cpu.step() // Execute DAA
        assertEquals(0x09u.toUByte(), cpu.A) // Adjusted value
    }

    @Test
    fun `DAA Addition with initial carry and half-carry flags set`() {
        val memory = UByteArrayMemory(0x0u..0xFFu)
        val cpu = LR35902(memory)

        cpu.A = 0x8Fu
        cpu.carryBit = true
        cpu.halfCarryBit = true
        memory.set(0x0Eu, 0x27u) // DAA opcode
        cpu.programCounter = 0xEu

        cpu.step() // Execute DAA
        assertEquals(0xF5u.toUByte(), cpu.A) // Adjusted value
    }

}