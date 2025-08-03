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
        memory.set(1u, 0x05u) // 0x__01
        memory.set(2u, 0x01u) // 0x05__

        cpu.programCounter = 0x0u
        cpu.step()

        assertEquals(0x105u.toUShort(), cpu.programCounter)
    }

    @Test
    fun `Verify 0x18 jump command moves program counter to correct address`() {
        val memory = UByteArrayMemory(0x0u..0xFFFFu)
        val cpu = LR35902(memory)
        memory.set(0u, 0x18u) // JR to
        memory.set(1u, 0x02u) // +2 bytes

        cpu.programCounter = 0x0u
        cpu.step()

        assertEquals(0x4u.toUShort(), cpu.programCounter)
    }

    @Test
    fun `Verify 0x18 jump negative command moves program counter to correct address`() {
        val memory = UByteArrayMemory(0x0u..0xFFFFu)
        val cpu = LR35902(memory)
        memory.set(0x10u, 0x18u) // JR to
        memory.set(0x11u, 0xF2u) // -14 bytes

        cpu.programCounter = 0x10u
        cpu.step()

        assertEquals(0x4u.toUShort(), cpu.programCounter)
    }


}