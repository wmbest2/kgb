package best.william.kgb.cpu

import kgb.util.ZERO
import kgb.util.bit
import kgb.util.withBit
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

@ExperimentalUnsignedTypes
class StatusBit(val register: KMutableProperty0<UByte>, val position: Int) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return register().bit(position)
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        register.set(register().withBit(position, value))
    }
}

@ExperimentalUnsignedTypes
fun flag(register: KMutableProperty0<UByte>, position: Int) = StatusBit(register, position)

@ExperimentalUnsignedTypes
class MaskedBits(val register: KMutableProperty0<UByte>, val mask: UByte) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): UByte {
        return register() and mask
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: UByte) {
        register.set((register() and mask.inv()) or (value and mask))
    }
}

@ExperimentalUnsignedTypes
fun masked(register: KMutableProperty0<UByte>, mask: UByte) = MaskedBits(register, mask)
