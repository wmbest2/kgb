package best.william.kgb.cpu

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


@ExperimentalUnsignedTypes
class StatusBitTest {
    var register: UByte = 0u

    var sb1 by StatusBit(::register, 0)
    var sb2 by StatusBit(::register, 1)
    var sb4 by StatusBit(::register, 2)
    var sb8 by StatusBit(::register, 3)
    var sb16 by StatusBit(::register, 4)
    var sb32 by StatusBit(::register, 5)
    var sb64 by StatusBit(::register, 6)
    var sb128 by StatusBit(::register, 7)

    @Test
    fun `Verify Status bits read correctly`() {
        register = 0xAAu // 10101010
        assertFalse(sb1)
        assertTrue(sb2)
        assertFalse(sb4)
        assertTrue(sb8)
        assertFalse(sb16)
        assertTrue(sb32)
        assertFalse(sb64)
        assertTrue(sb128)
        
        register = 0x55u // 01010101
        assertTrue(sb1)
        assertFalse(sb2)
        assertTrue(sb4)
        assertFalse(sb8)
        assertTrue(sb16)
        assertFalse(sb32)
        assertTrue(sb64)
        assertFalse(sb128)
    }
    
    @Test
    fun `Test 1s position in register`() {
        register = 0u
        assertFalse(sb1)
        
        sb1 = true
        
        assertTrue(sb1)
        assertEquals(1u.toUByte(), register)

        sb1 = false

        assertEquals(0u.toUByte(), register)
        assertFalse(sb1)
    }

    @Test
    fun `Test 2s position in register`() {
        register = 0u
        assertFalse(sb2)

        sb2 = true

        assertTrue(sb2)
        assertEquals(2u.toUByte(), register)

        sb2 = false

        assertEquals(0u.toUByte(), register)
        assertFalse(sb2)
    }

    @Test
    fun `Test 4s position in register`() {
        register = 0u
        assertFalse(sb4)

        sb4 = true

        assertTrue(sb4)
        assertEquals(4u.toUByte(), register)

        sb4 = false

        assertEquals(0u.toUByte(), register)
        assertFalse(sb4)
    }

    @Test
    fun `Test 8s position in register`() {
        register = 0u
        assertFalse(sb8)

        sb8 = true

        assertTrue(sb8)
        assertEquals(8u.toUByte(), register)

        sb8 = false

        assertEquals(0u.toUByte(), register)
        assertFalse(sb8)
    }

    @Test
    fun `Test 16s position in register`() {
        register = 0u
        assertFalse(sb16)

        sb16 = true

        assertTrue(sb16)
        assertEquals(16u.toUByte(), register)

        sb16 = false

        assertEquals(0u.toUByte(), register)
        assertFalse(sb16)
    }

    @Test
    fun `Test 32s position in register`() {
        register = 0u
        assertFalse(sb32)

        sb32 = true

        assertTrue(sb32)
        assertEquals(32u.toUByte(), register)

        sb32 = false

        assertEquals(0u.toUByte(), register)
        assertFalse(sb32)
    }

    @Test
    fun `Test 64s position in register`() {
        register = 0u
        assertFalse(sb64)

        sb64 = true

        assertTrue(sb64)
        assertEquals(64u.toUByte(), register)

        sb64 = false

        assertEquals(0u.toUByte(), register)
        assertFalse(sb64)
    }

    @Test
    fun `Test 128s position in register`() {
        register = 0u
        assertFalse(sb128)

        sb128 = true

        assertTrue(sb128)
        assertEquals(128u.toUByte(), register)

        sb128 = false

        assertEquals(0u.toUByte(), register)
        assertFalse(sb128)
    }
}