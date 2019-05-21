package best.william.kgb.cpu

import org.junit.Assert.*
import org.junit.Test

@ExperimentalUnsignedTypes
class StatusBitTest: StatusBit.Holder {
    override var statusRegister: UByte = 0u

    var sb1 by StatusBit(0)
    var sb2 by StatusBit(1)
    var sb4 by StatusBit(2)
    var sb8 by StatusBit(3)
    var sb16 by StatusBit(4)
    var sb32 by StatusBit(5)
    var sb64 by StatusBit(6)
    var sb128 by StatusBit(7)

    @Test
    fun `Verify Status bits read correctly`() {
        statusRegister = 0xAAu // 10101010
        assertFalse(sb1)
        assertTrue(sb2)
        assertFalse(sb4)
        assertTrue(sb8)
        assertFalse(sb16)
        assertTrue(sb32)
        assertFalse(sb64)
        assertTrue(sb128)
        
        statusRegister = 0x55u // 01010101
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
        statusRegister = 0u
        assertFalse(sb1)
        
        sb1 = true
        
        assertTrue(sb1)
        assertEquals(1u.toUByte(), statusRegister)

        sb1 = false

        assertEquals(0u.toUByte(), statusRegister)
        assertFalse(sb1)
    }

    @Test
    fun `Test 2s position in register`() {
        statusRegister = 0u
        assertFalse(sb2)

        sb2 = true

        assertTrue(sb2)
        assertEquals(2u.toUByte(), statusRegister)

        sb2 = false

        assertEquals(0u.toUByte(), statusRegister)
        assertFalse(sb2)
    }

    @Test
    fun `Test 4s position in register`() {
        statusRegister = 0u
        assertFalse(sb4)

        sb4 = true

        assertTrue(sb4)
        assertEquals(4u.toUByte(), statusRegister)

        sb4 = false

        assertEquals(0u.toUByte(), statusRegister)
        assertFalse(sb4)
    }

    @Test
    fun `Test 8s position in register`() {
        statusRegister = 0u
        assertFalse(sb8)

        sb8 = true

        assertTrue(sb8)
        assertEquals(8u.toUByte(), statusRegister)

        sb8 = false

        assertEquals(0u.toUByte(), statusRegister)
        assertFalse(sb8)
    }

    @Test
    fun `Test 16s position in register`() {
        statusRegister = 0u
        assertFalse(sb16)

        sb16 = true

        assertTrue(sb16)
        assertEquals(16u.toUByte(), statusRegister)

        sb16 = false

        assertEquals(0u.toUByte(), statusRegister)
        assertFalse(sb16)
    }

    @Test
    fun `Test 32s position in register`() {
        statusRegister = 0u
        assertFalse(sb32)

        sb32 = true

        assertTrue(sb32)
        assertEquals(32u.toUByte(), statusRegister)

        sb32 = false

        assertEquals(0u.toUByte(), statusRegister)
        assertFalse(sb32)
    }

    @Test
    fun `Test 64s position in register`() {
        statusRegister = 0u
        assertFalse(sb64)

        sb64 = true

        assertTrue(sb64)
        assertEquals(64u.toUByte(), statusRegister)

        sb64 = false

        assertEquals(0u.toUByte(), statusRegister)
        assertFalse(sb64)
    }

    @Test
    fun `Test 128s position in register`() {
        statusRegister = 0u
        assertFalse(sb128)

        sb128 = true

        assertTrue(sb128)
        assertEquals(128u.toUByte(), statusRegister)

        sb128 = false

        assertEquals(0u.toUByte(), statusRegister)
        assertFalse(sb128)
    }
}