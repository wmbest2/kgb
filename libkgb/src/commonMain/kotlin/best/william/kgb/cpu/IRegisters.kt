package best.william.kgb.cpu

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
        get() = asWord(F and 0xF0u, A)
        set(value) {
            A = (value.toUInt() shr 8).toUByte()
            F = (value and 0xF0u).toUByte()
        }

    var BC: UShort
        get() = asWord(C, B)
        set(value) {
            B = (value.toUInt() shr 8).toUByte()
            C = (value and 0xFFu).toUByte()
        }

    var DE: UShort
        get() = asWord(E, D)
        set(value) {
           D = (value.toUInt() shr 8).toUByte()
           E = (value and 0xFFu).toUByte()
        }

    var HL: UShort
        get() = asWord(L, H)
        set(value) {
            H = (value.toUInt() shr 8).toUByte()
            L = (value and 0xFFu).toUByte()
        }

    companion object {
        val US_256 = 256u.toUShort()
    }
}

data class Registers(
        override var A: UByte = 0u,
        override var B: UByte = 0u,
        override var C: UByte = 0u,
        override var D: UByte = 0u,
        override var E: UByte = 0u,
        override var H: UByte = 0u,
        override var L: UByte = 0u,
        var _F: UByte = 0u
): IRegisters {

    override var F: UByte
        get() = _F and 0xF0u // Keep only the upper nibble
        set(value) {
            _F = value and 0xF0u // Keep only the upper nibble
        }
}

fun asWord(low: UByte, high: UByte) =
    (((high.toUInt() shl 8) or low.toUInt()).toUShort())
