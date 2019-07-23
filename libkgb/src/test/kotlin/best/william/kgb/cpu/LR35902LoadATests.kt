package best.william.kgb.cpu

import kgb.memory.UByteArrayMemory
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalUnsignedTypes
class LR35902LoadATests {

    val memory = UByteArrayMemory(0x0u..0xFFFFu)
    lateinit var cpu: LR35902

    @Before
    fun setup() {
        cpu = LR35902(memory)
        cpu.programCounter = 0x0u
    }

    @Test
    fun `Load A into A (0x7F)`() {
        cpu.A = 0x23u

        memory[0u] = 0x7Fu

        cpu.step()
        assertEquals(0x23u.toUByte(), cpu.A)
    }

    @Test
    fun `Load B into A (0x78)`() {
        cpu.B = 0x23u

        memory[0u] = 0x78u

        cpu.step()
        assertEquals(0x23u.toUByte(), cpu.A)
    }

    @Test
    fun `Load C into A (0x79)`() {
        cpu.C = 0x23u

        memory[0u] = 0x79u

        cpu.step()
        assertEquals(0x23u.toUByte(), cpu.A)
    }

    @Test
    fun `Load D into A (0x7A)`() {
        cpu.D = 0x23u

        memory[0u] = 0x7Au

        cpu.step()
        assertEquals(cpu.D, cpu.A)
    }

    @Test
    fun `Load E into A (0x7B)`() {
        cpu.E = 0x23u

        memory[0u] = 0x7Bu

        cpu.step()
        assertEquals(0x23u.toUByte(), cpu.A)
    }

    @Test
    fun `Load H into A (0x7C)`() {
        cpu.H = 0x23u

        memory[0u] = 0x7Cu

        cpu.step()
        assertEquals(0x23u.toUByte(), cpu.A)
    }

    @Test
    fun `Load L into A (0x7D)`() {
        cpu.L = 0x23u

        memory[0u] = 0x7Du

        cpu.step()
        assertEquals(0x23u.toUByte(), cpu.A)
    }

    @Test
    fun `Load (HL) into A (0x7E)`() {
        cpu.H = 0x01u // 0x01__
        cpu.L = 0x23u // 0x0123

        memory[0u] = 0x7Eu
        memory[0x123u] = 0x18u

        cpu.step()
        assertEquals(memory[0x123u], cpu.A)
    }



    @Test
    fun `Load (nn) into A (0xFA)`() {
        memory[0u] = 0xFAu
        memory[1u] = 0x23u // 0x__23
        memory[2u] = 0x01u // 0x0123
        memory[0x123u] = 0x18u

        cpu.step()
        assertEquals(memory[0x123u], cpu.A)
    }

//    @Test
//    fun `Load (BC) into A (0x02)`() {
//        cpu.B = 0x01u // 0x01__
//        cpu.C = 0x23u // 0x0123
//
//        memory[0u] = 0x02u
//        memory[0x123u] = 0x18u
//
//        cpu.step()
//        assertEquals(0x18u.toUByte(), cpu.A)
//    }
//
//
//    @Test
//    fun `Load (DE) into A (0x12)`() {
//        cpu.D = 0x01u // 0x01__
//        cpu.E = 0x23u // 0x0123
//
//        memory[0u] = 0x12u
//        memory[0x123u] = 0x18u
//
//        cpu.step()
//        assertEquals(0x18u.toUByte(), cpu.A)
//    }


}
