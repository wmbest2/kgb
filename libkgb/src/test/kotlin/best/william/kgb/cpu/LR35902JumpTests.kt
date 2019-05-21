package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import org.junit.Assert.*
import org.junit.Test

@ExperimentalUnsignedTypes
class LR35902JumpTests {

    @Test
    fun `Verify 0xC3 jump command moves program counter to correct address`() {
        val memory = UByteArrayMemory(0xFFFFu)
        val cpu = LR35902(memory)
        memory[0u] = 0xC3u // JMP to
        memory[1u] = 0x05u // 0x__05
        memory[2u] = 0x01u // 0x0105

        cpu.programCounter = 0x0u
        cpu.step()

        assertEquals(0x105u.toUShort(), cpu.programCounter)
    }

}