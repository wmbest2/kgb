package kgb.util

@ExperimentalUnsignedTypes
private val ubyteMasks: Array<UByte> = arrayOf(
        1u,
        2u,
        4u,
        8u,
        16u,
        32u,
        64u,
        128u
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
fun UByte.bit(bit: Int): Boolean = this.and(ubyteMasks[bit]) == ubyteMasks[bit]

@ExperimentalUnsignedTypes
fun UByte.swapNibbles(): UByte {
    val top = (this and 0xF0u) / 16u
    val bottom = (this and 0x0Fu) * 16u

    return  (top or bottom).toUByte()
}

