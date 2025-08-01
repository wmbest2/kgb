package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class LR35902SubTests {
    @Test
    fun `SUB A, B basic subtraction`() {
        val memory = UByteArrayMemory(0x0u..0xFFFFu)
        val cpu = LR35902(memory)
        cpu.A = 0x10u
        cpu.B = 0x05u
        memory.set(cpu.programCounter, 0x90u) // SUB A, B opcode
        cpu.step()
        assertEquals(0x0Bu, cpu.A)
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit) // xor-based formula: ((0x10 xor 0x05 xor 0x0B) & 0x10) != 0
        assertEquals(true, cpu.subtractBit)
    }

    @Test
    fun `SUB A, B zero result`() {
        val memory = UByteArrayMemory(0x0u..0xFFFFu)
        val cpu = LR35902(memory)
        cpu.A = 0x05u
        cpu.B = 0x05u
        memory.set(cpu.programCounter, 0x90u) // SUB A, B opcode
        cpu.step()
        assertEquals(0x00u, cpu.A)
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(false, cpu.halfCarryBit) // xor-based formula: ((0x05 xor 0x05 xor 0x00) & 0x10) == 0
        assertEquals(true, cpu.subtractBit)
    }

    @Test
    fun `SUB A, B with carry`() {
        val memory = UByteArrayMemory(0x0u..0xFFFFu)
        val cpu = LR35902(memory)
        cpu.A = 0x05u
        cpu.B = 0x10u
        memory.set(cpu.programCounter, 0x90u) // SUB A, B opcode
        cpu.step()
        assertEquals(0xF5u, cpu.A)
        assertEquals(false, cpu.zeroBit)
        assertEquals(true, cpu.carryBit)
        assertEquals(false, cpu.halfCarryBit) // xor-based formula: ((0x05 xor 0x10 xor 0xF5) & 0x10) != 0
        assertEquals(true, cpu.subtractBit)
    }

    @Test
    fun `SBC A, B no carry subtraction`() {
        val memory = UByteArrayMemory(0x0u..0xFFFFu)
        val cpu = LR35902(memory)
        cpu.A = 0x10u
        cpu.B = 0x05u
        cpu.carryBit = false
        memory.set(cpu.programCounter, 0x98u) // SBC A, B opcode
        cpu.step()
        assertEquals(0x0Bu, cpu.A)
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit) // xor-based formula: ((0x10 xor 0x05 xor 0x0B) & 0x10) != 0
        assertEquals(true, cpu.subtractBit)
    }

    @Test
    fun `SBC A, B with carry subtraction`() {
        val memory = UByteArrayMemory(0x0u..0xFFFFu)
        val cpu = LR35902(memory)
        cpu.A = 0x10u
        cpu.B = 0x05u
        cpu.carryBit = true
        memory.set(cpu.programCounter, 0x98u) // SBC A, B opcode
        cpu.step()
        assertEquals(0x0Au, cpu.A)
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit) // xor-based formula: ((0x10 xor 0x05 xor 0x0A) & 0x10) != 0
        assertEquals(true, cpu.subtractBit)
    }

    @Test
    fun `SBC A, B with carry and borrow`() {
        val memory = UByteArrayMemory(0x0u..0xFFFFu)
        val cpu = LR35902(memory)
        cpu.A = 0x05u
        cpu.B = 0x10u
        cpu.carryBit = true
        memory.set(cpu.programCounter, 0x98u) // SBC A, B opcode
        cpu.step()
        assertEquals(0xF4u, cpu.A)
        assertEquals(false, cpu.zeroBit)
        assertEquals(true, cpu.carryBit)
        assertEquals(false, cpu.halfCarryBit) // xor-based formula: ((0x05 xor 0x10 xor 0xF4) & 0x10) != 0
        assertEquals(true, cpu.subtractBit)
    }
}
