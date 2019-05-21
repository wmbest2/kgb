package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import org.junit.Assert.*
import org.junit.Test

@ExperimentalUnsignedTypes
class LR35902CommandTests {

    @Test
    fun `NoOp just increments the counter`() {

        val memory = UByteArrayMemory(0xFFu)
        val cpu = LR35902(memory)

        cpu.programCounter = 0x0u
        cpu.step()

        assertEquals(0x1u.toUShort(), cpu.programCounter)
    }

    @Test
    fun `Halt (0x76) should stop operations until interrupt occurs`() {
        val memory = UByteArrayMemory(0xFFu)
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

        val memory = UByteArrayMemory(0xFFu)
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

        val memory = UByteArrayMemory(0xFFu)
        val cpu = LR35902(memory)

        cpu.programCounter = 0xAu
        cpu.zeroBit = false

        memory[0xAu] = 0x20u
        memory[0xBu] = 0x03u

        cpu.step()

        assertEquals(0xFu.toUShort(), cpu.programCounter)

    }

    @Test
    fun `JR NZ, r8 should do nothing the PC if Z`() {

        val memory = UByteArrayMemory(0xFFu)
        val cpu = LR35902(memory)

        cpu.programCounter = 0xAu
        cpu.zeroBit = true

        memory[0xAu] = 0x20u
        memory[0xBu] = (-3).toUByte()

        cpu.step()

        assertEquals(0xCu.toUShort(), cpu.programCounter)

    }

    @Test
    fun `JR NZ, r8 should increment the PC if Z`() {

        val memory = UByteArrayMemory(0xFFu)
        val cpu = LR35902(memory)

        cpu.programCounter = 0xAu
        cpu.zeroBit = true

        memory[0xAu] = 0x20u
        memory[0xBu] = 0x03u

        cpu.step()

        assertEquals(0xCu.toUShort(), cpu.programCounter)

    }

}