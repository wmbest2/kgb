package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalUnsignedTypes
class LR35902JumpTests {

    @Test
    fun `Verify 0xC3 jump command moves program counter to correct address`() {
        val memory = UByteArrayMemory(0x0u..0xFFFFu)
        val cpu = LR35902(memory)
        memory.set(0u, 0xC3u) // JMP to
        memory.set(1u, 0x01u) // 0x01__
        memory.set(2u, 0x05u) // 0x__05

        cpu.programCounter = 0x0u
        cpu.step()

        assertEquals(0x105u.toUShort(), cpu.programCounter)
    }

}