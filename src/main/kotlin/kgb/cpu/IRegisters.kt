package best.william.kgb.cpu

import sun.jvm.hotspot.code.CompressedStream.L
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

@ExperimentalUnsignedTypes
interface IRegisters: StatusBit.Holder {
    var A: UByte
    var B: UByte
    var C: UByte
    var D: UByte
    var E: UByte
    var H: UByte
    var L: UByte

    var AF: UShort
        get() = asWord(A, statusRegister)
        set(value) {
            val valueInt = value.toUInt()
            A = (valueInt shr 8).toUByte()
            statusRegister = (valueInt and 0xFFu).toUByte()
        }

    var BC: UShort
        get() = asWord(C, B)
        set(value) {
            val valueInt = value.toUInt()
            B = (valueInt shr 8).toUByte()
            C = (valueInt and 0xFFu).toUByte()
        }

    var DE: UShort
        get() = asWord(E, D)
        set(value) {
            val valueInt = value.toUInt()
            D = (valueInt shr 8).toUByte()
            E = (valueInt and 0xFFu).toUByte()
        }

    var HL: UShort
        get() = asWord(L, H)
        set(value) {
            val valueInt = value.toUInt()
            H = (valueInt shr 8).toUByte()
            L = (valueInt and 0xFFu).toUByte()
        }
}

@ExperimentalUnsignedTypes
data class Registers(
        override var A: UByte = 0u,
        override var B: UByte = 0u,
        override var C: UByte = 0u,
        override var D: UByte = 0u,
        override var E: UByte = 0u,
        override var H: UByte = 0u,
        override var L: UByte = 0u,
        override var statusRegister: UByte = 0u
): IRegisters

@ExperimentalUnsignedTypes
fun asWord(low: UByte, high: UByte) =
        high.toUInt().shl(8).or(low.toUInt()).toUShort()

