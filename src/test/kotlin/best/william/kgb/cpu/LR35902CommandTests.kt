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

}