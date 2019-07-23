package kgb.util

@ExperimentalUnsignedTypes
private val ushortMasks: Array<UShort> = arrayOf(
        0x0001u, 0x0002u, 0x0004u, 0x0008u,
        0x0010u, 0x0020u, 0x0040u, 0x0080u,
        0x0010u, 0x0200u, 0x0400u, 0x0800u,
        0x1000u, 0x2000u, 0x4000u, 0x8000u
)



@ExperimentalUnsignedTypes
private val ushortInvertedMasks: Array<UShort> = ushortMasks.map { it.inv() }.toTypedArray()

@ExperimentalUnsignedTypes
val UShort.Companion.masks
    get() = ushortMasks

@ExperimentalUnsignedTypes
val UShort.Companion.invertedMasks
    get() = ushortInvertedMasks

@ExperimentalUnsignedTypes
fun UShort.withBit(bit: Int, value: Boolean) =
        if (value) {
            this.or(ushortMasks[bit])
        } else {
            this.and(ushortInvertedMasks[bit])
        }

@ExperimentalUnsignedTypes
fun UShort.bit(bit: Int): Boolean = this.and(ushortMasks[bit]) == ushortMasks[bit]

@ExperimentalUnsignedTypes
val UShort.bytes
    get() = highByte to lowByte

@ExperimentalUnsignedTypes
val UShort.highByte
    get() = ((this.toUInt() and 0xFF00u) shr 8).toUByte()

@ExperimentalUnsignedTypes
val UShort.lowByte
    get() = (this and 0x00FFu.toUShort()).toUByte()
