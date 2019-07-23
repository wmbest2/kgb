package best.william.kgb.cpu

@ExperimentalUnsignedTypes
interface IRegisters {
    var A: UByte
    var F: UByte
    var B: UByte
    var C: UByte
    var D: UByte
    var E: UByte
    var H: UByte
    var L: UByte

    var AF: UShort
        get() = asWord(A, F)
        set(value) {
            A = (value / 256u).toUByte()
            F = (value and 0xFFu).toUByte()
        }

    var BC: UShort
        get() = asWord(C, B)
        set(value) {
            B = (value / 256u).toUByte()
            C = (value and 0xFFu).toUByte()
        }

    var DE: UShort
        get() = asWord(E, D)
        set(value) {
           D = (value / 256u).toUByte()
           E = (value and 0xFFu).toUByte()
        }

    var HL: UShort
        get() = asWord(L, H)
        set(value) {
            H = (value / 256u).toUByte()
            L = (value and 0xFFu).toUByte()
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
        override var F: UByte = 0u
): IRegisters

@ExperimentalUnsignedTypes
fun asWord(low: UByte, high: UByte) =
        (high * 256u).toUShort().or(low.toUShort())

