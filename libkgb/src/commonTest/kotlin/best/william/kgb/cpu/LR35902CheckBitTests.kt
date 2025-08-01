package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import kgb.util.masks
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.assertEquals
import kotlin.time.measureTime

@ExperimentalUnsignedTypes
class LR35902CheckBitTests {

    val memory = UByteArrayMemory(0x0u..0xFFFFu)
    var cpu = LR35902(memory)

    @BeforeTest
    fun setup() {
        cpu.programCounter = 0x0u

        memory.set(0u, 0xCBu)

        // Give HL an address to use
        val time = measureTime {
            cpu.HL = 0x0123u
        }
        println("Time to reset HL $time")
    }

    @Test
    fun `BIT 0, A = False`() {
        cpu.A = 0x0u

        memory.set(1u, 0x47u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)
    }
    
    @Test
    fun `BIT 1, A = False`() {
        cpu.A = 0x0u

        memory.set(1u, 0x4Fu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)
    }

    @Test
    fun `BIT 2, A = False`() {
        cpu.A = 0x0u

        memory.set(1u, 0x57u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, A = False`() {
        cpu.A = 0x0u

        memory.set(1u, 0x5Fu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, A = False`() {
        cpu.A = 0x0u

        memory.set(1u, 0x67u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, A = False`() {
        cpu.A = 0x0u

        memory.set(1u, 0x6Fu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, A = False`() {
        cpu.A = 0x0u

        memory.set(1u, 0x77u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, A = False`() {
        cpu.A = 0x0u

        memory.set(1u, 0x7Fu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 0, B = False`() {
        cpu.B = 0x0u

        memory.set(1u, 0x40u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, B = False`() {
        cpu.B = 0x0u

        memory.set(1u, 0x48u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, B = False`() {
        cpu.B = 0x0u

        memory.set(1u, 0x50u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, B = False`() {
        cpu.B = 0x0u

        memory.set(1u, 0x58u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, B = False`() {
        cpu.B = 0x0u

        memory.set(1u, 0x60u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, B = False`() {
        cpu.B = 0x0u

        memory.set(1u, 0x68u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, B = False`() {
        cpu.B = 0x0u

        memory.set(1u, 0x70u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, B = False`() {
        cpu.B = 0x0u

        memory.set(1u, 0x78u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 0, C = False`() {
        cpu.C = 0x0u

        memory.set(1u, 0x41u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, C = False`() {
        cpu.C = 0x0u

        memory.set(1u, 0x49u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, C = False`() {
        cpu.C = 0x0u

        memory.set(1u, 0x51u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, C = False`() {
        cpu.C = 0x0u

        memory.set(1u, 0x59u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, C = False`() {
        cpu.C = 0x0u

        memory.set(1u, 0x61u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, C = False`() {
        cpu.C = 0x0u

        memory.set(1u, 0x69u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, C = False`() {
        cpu.C = 0x0u

        memory.set(1u, 0x71u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, C = False`() {
        cpu.C = 0x0u

        memory.set(1u, 0x79u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 0, D = False`() {
        cpu.D = 0x0u

        memory.set(1u, 0x42u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, D = False`() {
        cpu.D = 0x0u

        memory.set(1u, 0x4Au)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, D = False`() {
        cpu.D = 0x0u

        memory.set(1u, 0x52u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, D = False`() {
        cpu.D = 0x0u

        memory.set(1u, 0x5Au)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, D = False`() {
        cpu.D = 0x0u

        memory.set(1u, 0x62u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, D = False`() {
        cpu.D = 0x0u

        memory.set(1u, 0x6Au)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, D = False`() {
        cpu.D = 0x0u

        memory.set(1u, 0x72u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, D = False`() {
        cpu.D = 0x0u

        memory.set(1u, 0x7Au)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }


    @Test
    fun `BIT 0, E = False`() {
        cpu.E = 0x0u

        memory.set(1u, 0x43u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, E = False`() {
        cpu.E = 0x0u

        memory.set(1u, 0x4Bu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, E = False`() {
        cpu.E = 0x0u

        memory.set(1u, 0x53u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, E = False`() {
        cpu.E = 0x0u

        memory.set(1u, 0x5Bu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, E = False`() {
        cpu.E = 0x0u

        memory.set(1u, 0x63u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, E = False`() {
        cpu.E = 0x0u

        memory.set(1u, 0x6Bu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, E = False`() {
        cpu.E = 0x0u

        memory.set(1u, 0x73u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, E = False`() {
        cpu.E = 0x0u

        memory.set(1u, 0x7Bu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 0, H = False`() {
        cpu.H = 0x0u

        memory.set(1u, 0x44u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, H = False`() {
        cpu.H = 0x0u

        memory.set(1u, 0x4Cu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, H = False`() {
        cpu.H = 0x0u

        memory.set(1u, 0x54u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, H = False`() {
        cpu.H = 0x0u

        memory.set(1u, 0x5Cu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, H = False`() {
        cpu.H = 0x0u

        memory.set(1u, 0x64u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, H = False`() {
        cpu.H = 0x0u

        memory.set(1u, 0x6Cu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, H = False`() {
        cpu.H = 0x0u

        memory.set(1u, 0x74u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, H = False`() {
        cpu.H = 0x0u

        memory.set(1u, 0x7Cu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 0, L = False`() {
        cpu.L = 0x0u

        memory.set(1u, 0x45u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, L = False`() {
        cpu.L = 0x0u

        memory.set(1u, 0x4Du)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, L = False`() {
        cpu.L = 0x0u

        memory.set(1u, 0x55u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, L = False`() {
        cpu.L = 0x0u

        memory.set(1u, 0x5Du)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, L = False`() {
        cpu.L = 0x0u

        memory.set(1u, 0x65u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, L = False`() {
        cpu.L = 0x0u

        memory.set(1u, 0x6Du)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, L = False`() {
        cpu.L = 0x0u

        memory.set(1u, 0x75u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, L = False`() {
        cpu.L = 0x0u

        memory.set(1u, 0x7Du)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 0, (HL) = False`() {
        memory.set(cpu.HL, 0x0u)

        memory.set(1u, 0x46u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, (HL) = False`() {
        memory.set(cpu.HL, 0x0u)

        memory.set(1u, 0x4Eu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, (HL) = False`() {
        memory.set(cpu.HL, 0x0u)

        memory.set(1u, 0x56u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, (HL) = False`() {
        memory.set(cpu.HL, 0x0u)

        memory.set(1u, 0x5Eu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, (HL) = False`() {
        memory.set(cpu.HL, 0x0u)

        memory.set(1u, 0x66u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, (HL) = False`() {
        memory.set(cpu.HL, 0x0u)

        memory.set(1u, 0x6Eu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, (HL) = False`() {
        memory.set(cpu.HL, 0x0u)

        memory.set(1u, 0x76u)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, (HL) = False`() {
        memory.set(cpu.HL, 0x0u)

        memory.set(1u, 0x7Eu)

        cpu.step()
        assertEquals(true, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 0, A = True`() {
        cpu.A = UByte.masks[0]

        memory.set(1u, 0x47u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)
    }

    @Test
    fun `BIT 1, A = True`() {
        cpu.A = UByte.masks[1]

        memory.set(1u, 0x4Fu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)
    }

    @Test
    fun `BIT 2, A = True`() {
        cpu.A = UByte.masks[2]

        memory.set(1u, 0x57u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, A = True`() {
        cpu.A = UByte.masks[3]

        memory.set(1u, 0x5Fu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, A = True`() {
        cpu.A = UByte.masks[4]

        memory.set(1u, 0x67u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, A = True`() {
        cpu.A = UByte.masks[5]

        memory.set(1u, 0x6Fu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, A = True`() {
        cpu.A = UByte.masks[6]

        memory.set(1u, 0x77u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, A = True`() {
        cpu.A = UByte.masks[7]

        memory.set(1u, 0x7Fu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 0, B = True`() {
        cpu.B = UByte.masks[0]

        memory.set(1u, 0x40u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, B = True`() {
        cpu.B = UByte.masks[1]

        memory.set(1u, 0x48u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, B = True`() {
        cpu.B = UByte.masks[2]

        memory.set(1u, 0x50u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, B = True`() {
        cpu.B = UByte.masks[3]

        memory.set(1u, 0x58u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, B = True`() {
        cpu.B = UByte.masks[4]

        memory.set(1u, 0x60u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, B = True`() {
        cpu.B = UByte.masks[5]

        memory.set(1u, 0x68u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, B = True`() {
        cpu.B = UByte.masks[6]

        memory.set(1u, 0x70u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, B = True`() {
        cpu.B = UByte.masks[7]

        memory.set(1u, 0x78u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 0, C = True`() {
        cpu.C = UByte.masks[0]

        memory.set(1u, 0x41u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, C = True`() {
        cpu.C = UByte.masks[1]

        memory.set(1u, 0x49u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, C = True`() {
        cpu.C = UByte.masks[2]

        memory.set(1u, 0x51u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, C = True`() {
        cpu.C = UByte.masks[3]

        memory.set(1u, 0x59u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, C = True`() {
        cpu.C = UByte.masks[4]

        memory.set(1u, 0x61u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, C = True`() {
        cpu.C = UByte.masks[5]

        memory.set(1u, 0x69u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, C = True`() {
        cpu.C = UByte.masks[6]

        memory.set(1u, 0x71u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, C = True`() {
        cpu.C = UByte.masks[7]

        memory.set(1u, 0x79u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 0, D = True`() {
        cpu.D = UByte.masks[0]

        memory.set(1u, 0x42u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, D = True`() {
        cpu.D = UByte.masks[1]

        memory.set(1u, 0x4Au)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, D = True`() {
        cpu.D = UByte.masks[2]

        memory.set(1u, 0x52u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, D = True`() {
        cpu.D = UByte.masks[3]

        memory.set(1u, 0x5Au)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, D = True`() {
        cpu.D = UByte.masks[4]

        memory.set(1u, 0x62u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, D = True`() {
        cpu.D = UByte.masks[5]

        memory.set(1u, 0x6Au)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, D = True`() {
        cpu.D = UByte.masks[6]

        memory.set(1u, 0x72u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, D = True`() {
        cpu.D = UByte.masks[7]

        memory.set(1u, 0x7Au)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }


    @Test
    fun `BIT 0, E = True`() {
        cpu.E = UByte.masks[0]

        memory.set(1u, 0x43u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, E = True`() {
        cpu.E = UByte.masks[1]

        memory.set(1u, 0x4Bu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, E = True`() {
        cpu.E = UByte.masks[2]

        memory.set(1u, 0x53u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, E = True`() {
        cpu.E = UByte.masks[3]

        memory.set(1u, 0x5Bu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, E = True`() {
        cpu.E = UByte.masks[4]

        memory.set(1u, 0x63u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, E = True`() {
        cpu.E = UByte.masks[5]

        memory.set(1u, 0x6Bu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, E = True`() {
        cpu.E = UByte.masks[6]

        memory.set(1u, 0x73u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, E = True`() {
        cpu.E = UByte.masks[7]

        memory.set(1u, 0x7Bu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 0, H = True`() {
        cpu.H = UByte.masks[0]

        memory.set(1u, 0x44u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, H = True`() {
        cpu.H = UByte.masks[1]

        memory.set(1u, 0x4Cu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, H = True`() {
        cpu.H = UByte.masks[2]

        memory.set(1u, 0x54u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, H = True`() {
        cpu.H = UByte.masks[3]

        memory.set(1u, 0x5Cu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, H = True`() {
        cpu.H = UByte.masks[4]

        memory.set(1u, 0x64u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, H = True`() {
        cpu.H = UByte.masks[5]

        memory.set(1u, 0x6Cu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, H = True`() {
        cpu.H = UByte.masks[6]

        memory.set(1u, 0x74u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, H = True`() {
        cpu.H = UByte.masks[7]

        memory.set(1u, 0x7Cu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 0, L = True`() {
        cpu.L = UByte.masks[0]

        memory.set(1u, 0x45u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, L = True`() {
        cpu.L = UByte.masks[1]

        memory.set(1u, 0x4Du)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, L = True`() {
        cpu.L = UByte.masks[2]

        memory.set(1u, 0x55u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, L = True`() {
        cpu.L = UByte.masks[3]

        memory.set(1u, 0x5Du)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, L = True`() {
        cpu.L = UByte.masks[4]

        memory.set(1u, 0x65u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, L = True`() {
        cpu.L = UByte.masks[5]

        memory.set(1u, 0x6Du)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, L = True`() {
        cpu.L = UByte.masks[6]

        memory.set(1u, 0x75u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, L = True`() {
        cpu.L = UByte.masks[7]

        memory.set(1u, 0x7Du)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 0, (HL) = True`() {
        memory.set(cpu.HL, UByte.masks[0])

        memory.set(1u, 0x46u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 1, (HL) = True`() {
        memory.set(cpu.HL, UByte.masks[1])

        memory.set(1u, 0x4Eu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 2, (HL) = True`() {
        memory.set(cpu.HL, UByte.masks[2])

        memory.set(1u, 0x56u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 3, (HL) = True`() {
        memory.set(cpu.HL, UByte.masks[3])

        memory.set(1u, 0x5Eu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 4, (HL) = True`() {
        memory.set(cpu.HL, UByte.masks[4])

        memory.set(1u, 0x66u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 5, (HL) = True`() {
        memory.set(cpu.HL, UByte.masks[5])

        memory.set(1u, 0x6Eu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 6, (HL) = True`() {
        memory.set(cpu.HL, UByte.masks[6])

        memory.set(1u, 0x76u)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }

    @Test
    fun `BIT 7, (HL) = True`() {
        memory.set(cpu.HL, UByte.masks[7])

        memory.set(1u, 0x7Eu)

        cpu.step()
        assertEquals(false, cpu.zeroBit)
        assertEquals(false, cpu.carryBit)
        assertEquals(true, cpu.halfCarryBit)

    }
}
