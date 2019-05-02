package best.william.kgb.cpu

import kgb.util.bit
import kgb.util.withBit
import kotlin.reflect.KProperty

@ExperimentalUnsignedTypes
class StatusBit(val position: Int) {
    operator fun getValue(thisRef: Holder, property: KProperty<*>): Boolean {
        return thisRef.statusRegister.bit(position)
    }

    operator fun setValue(thisRef: Holder, property: KProperty<*>, value: Boolean) {
        thisRef.statusRegister = thisRef.statusRegister.withBit(position, value)
    }

    interface Holder {
        var statusRegister: UByte
    }
}

fun flag(position: Int) = StatusBit(position)
