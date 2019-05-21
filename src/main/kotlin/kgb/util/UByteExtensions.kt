package kgb.util

@ExperimentalUnsignedTypes
private val ubyteMasks: Array<UByte> = arrayOf(
        0x01u, 0x02u, 0x04u, 0x08u,
        0x10u, 0x20u, 0x40u, 0x80u
)

@ExperimentalUnsignedTypes
private val ubyteInvertedMasks: Array<UByte> = ubyteMasks.map { it.inv() }.toTypedArray()

@ExperimentalUnsignedTypes
val UByte.Companion.masks
    get() = ubyteMasks

@ExperimentalUnsignedTypes
val UByte.Companion.invertedMasks
    get() = ubyteInvertedMasks

@ExperimentalUnsignedTypes
fun UByte.withBit(bit: Int, value: Boolean) =
        if (value) {
            this.or(ubyteMasks[bit])
        } else {
            this.and(ubyteInvertedMasks[bit])
        }

@ExperimentalUnsignedTypes
fun UByte.bit(bit: Int): Boolean {
    val mask = ubyteMasks[bit]
    return this.and(mask) == mask
}

@ExperimentalUnsignedTypes
fun UByte.swapNibbles(): UByte {
    val top = (this and 0xF0u) / 16u
    val bottom = (this and 0x0Fu) * 16u

    return  (top or bottom).toUByte()
}

@ExperimentalUnsignedTypes
val UByte.Companion.ZERO: UByte
    get() = 0u