@file:Suppress("NOTHING_TO_INLINE")
// These are called very frequently, so we want to avoid the overhead of function calls
// and use inline functions instead.

package best.william.kgb.cpu

import kgb.util.bit
import kgb.util.withBit
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

@ExperimentalUnsignedTypes
class StatusBit(val register: KMutableProperty0<UByte>, val position: Int) {
    inline operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return register().bit(position)
    }

    inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        register.set(register().withBit(position, value))
    }
}

@ExperimentalUnsignedTypes
inline fun flag(register: KMutableProperty0<UByte>, position: Int) = StatusBit(register, position)

@ExperimentalUnsignedTypes
class MaskedBits(val register: KMutableProperty0<UByte>, val mask: UByte) {
    inline operator fun getValue(thisRef: Any?, property: KProperty<*>): UByte {
        return register() and mask
    }

    inline operator fun setValue(thisRef: Any?, property: KProperty<*>, value: UByte) {
        register.set((register() and mask.inv()) or (value and mask))
    }
}

@ExperimentalUnsignedTypes
inline fun masked(register: KMutableProperty0<UByte>, mask: UByte) = MaskedBits(register, mask)
