@file:Suppress("NOTHING_TO_INLINE")
// These are called very frequently, so we want to avoid the overhead of function calls
// and use inline functions instead.

package best.william.kgb.cpu

import kgb.util.bit
import kgb.util.withBit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

class StatusBit(val register: KMutableProperty0<UByte>, val position: Int) {
    inline operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return register().bit(position)
    }

    inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        register.set(register().withBit(position, value))
    }
}

inline fun flag(register: KMutableProperty0<UByte>, position: Int) = StatusBit(register, position)

class MaskedBits(val register: KMutableProperty0<UByte>, val mask: UByte): ReadWriteProperty<Any?, UByte> {
    override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): UByte {
        return register.get() and mask
    }

    override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: UByte) {
        register.set((register() and mask.inv()) or (value and mask))
    }
}

class ShiftedMaskedBits(
    val register: KMutableProperty0<UByte>,
    val mask: UByte,
    val shift: Int
): ReadWriteProperty<Any?, UByte> {
    override inline operator fun getValue(thisRef: Any?, property: KProperty<*>): UByte {
        return ((register() and mask).toInt() shr shift).toUByte()
    }

    override inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: UByte) {
        register.set((register() and mask.inv()) or ((value.toInt() shl shift).toUByte() and mask))
    }
}

inline fun masked(register: KMutableProperty0<UByte>, mask: UByte, shift: Int = 0): ReadWriteProperty<Any?, UByte> {
    return if (shift != 0) {
        ShiftedMaskedBits(register, mask, shift)
    } else {
        MaskedBits(register, mask)
    }
}
