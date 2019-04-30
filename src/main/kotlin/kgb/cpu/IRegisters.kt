package best.william.kgb.cpu

@ExperimentalUnsignedTypes
interface IRegisters: StatusBit.Holder {
    var A: UByte
    var B: UByte
    var C: UByte
    var D: UByte
    var E: UByte
    var H: UByte
    var L: UByte

    val BC: UShort
        get() = asWord(C, B)

    val DE: UShort
        get() = asWord(E, D)

    val HL: UShort
        get() = asWord(L, H)
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

