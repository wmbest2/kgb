package best.william.kgb.cpu

import kotlin.reflect.KProperty

@ExperimentalUnsignedTypes
class StatusBit(position: Int) {
    private val mask: UByte = (1u shl position).toUByte()
    private val invMask = mask.inv()

    operator fun getValue(thisRef: Holder, property: KProperty<*>): Boolean {
        return thisRef.statusRegister.and(mask) != 0.toUByte()
    }

    operator fun setValue(thisRef: Holder, property: KProperty<*>, value: Boolean) {
        thisRef.statusRegister = if (value) {
            thisRef.statusRegister.or(mask)
        } else {
            thisRef.statusRegister.and(invMask)
        }
    }

    interface Holder {
        var statusRegister: UByte
    }
}

fun flag(position: Int) = StatusBit(position)
