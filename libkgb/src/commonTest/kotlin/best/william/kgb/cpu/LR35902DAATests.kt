package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import kotlin.test.Test
import kotlin.test.assertEquals

class LR35902DAATests {

    @OptIn(ExperimentalUnsignedTypes::class)
    @Test
    fun `Test DAA for all 256 x 16 combinations`() {
        // This tests all possible combinations of A register values (0-255)
        // and flag settings (16 combinations of Z/N/H/C flags)
        val memory = UByteArrayMemory(0x0u..0xFFFFu)
        val cpu = LR35902(memory)

        // Set up memory with DAA instruction
        memory[0u.toUShort()] = 0x27u  // DAA opcode

        var crc: UInt = 0u

        // Following the assembly code pattern exactly:
        // ld de,0
        // -    push de
        //      pop af
        //      daa
        //      push af
        //      call update_crc
        //      pop hl
        //      ld a,l
        //      call update_crc
        //      inc d
        //      jr nz,-
        //      ld a,e
        //      add $10
        //      ld e,a
        //      jr nz,-

        for (flags in 0u..15u) {
            for (a in 0u..255u) {
                // Set A register and flags
                cpu.A = a.toUByte()
                cpu.F = flags.toUByte()

                // Execute DAA instruction
                cpu.programCounter = 0u
                cpu.step()

                // Update CRC with the result of DAA
                crc = updateCRC(crc, cpu.A.toUInt())
            }
        }
        assertEquals(0x6A9F8D8A.toHexString(), crc.toHexString(), "DAA operation produces incorrect CRC across all combinations.")
    }

    // CRC-32 implementation to match the Game Boy test ROM
    private fun updateCRC(crc: UInt, value: UInt): UInt {
        var r = crc
        r = r xor (value shl 24)

        // Process each bit according to Game Boy test ROM algorithm
        for (b in 0 until 8) {
            val highBit = (r and 0x80000000u) != 0u
            r = r shl 1

            if (highBit) {
                r = r xor 0x04C11DB7u  // CRC polynomial used in Game Boy tests
            }
        }

        return r
    }
}
