package kgb.util

@ExperimentalUnsignedTypes
val ubyteMasks: Array<UByte> = arrayOf(
        0x01u, 0x02u, 0x04u, 0x08u,
        0x10u, 0x20u, 0x40u, 0x80u
)

@ExperimentalUnsignedTypes
val ubyteInvertedMasks: Array<UByte> = ubyteMasks.map { it.inv() }.toTypedArray()

@ExperimentalUnsignedTypes
val UByte.Companion.masks
    get() = ubyteMasks

@ExperimentalUnsignedTypes
val UByte.Companion.invertedMasks
    get() = ubyteInvertedMasks

@ExperimentalUnsignedTypes
inline fun UByte.withBit(bit: Int, value: Boolean): UByte{
    return if (value) (this or (1u shl bit).toUByte()) else (this and (1u shl bit).toUByte().inv())
}

@ExperimentalUnsignedTypes
inline fun UByte.bit(bit: Int): Boolean {
    return (this.toInt() shr bit) and 1 != 0
}

@ExperimentalUnsignedTypes
fun UByte.swapNibbles(): UByte {
    return ((this.toUInt() shl 4) or (this.toUInt() shr 4)).toUByte()
}

@ExperimentalUnsignedTypes
val UByte.Companion.ZERO: UByte
    get() = 0u

