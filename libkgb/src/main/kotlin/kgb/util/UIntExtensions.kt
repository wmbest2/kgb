package kgb.util

@ExperimentalUnsignedTypes
private val uintMasks: Array<UInt> = arrayOf(
        0x00000001u, 0x00000002u, 0x00000004u, 0x00000008u,
        0x00000010u, 0x00000020u, 0x00000040u, 0x00000080u,
        0x00000100u, 0x00000200u, 0x00000400u, 0x00000800u,
        0x00001000u, 0x00002000u, 0x00004000u, 0x00008000u,
        0x00010000u, 0x00020000u, 0x00040000u, 0x00080000u,
        0x00100000u, 0x00200000u, 0x00400000u, 0x00800000u,
        0x01000000u, 0x02000000u, 0x04000000u, 0x08000000u,
        0x10000000u, 0x20000000u, 0x40000000u, 0x80000000u
)



@ExperimentalUnsignedTypes
private val uintInvertedMasks: Array<UInt> = uintMasks.map { it.inv() }.toTypedArray()

@ExperimentalUnsignedTypes
val UInt.Companion.masks
    get() = uintMasks

@ExperimentalUnsignedTypes
val UInt.Companion.invertedMasks
    get() = uintInvertedMasks

@ExperimentalUnsignedTypes
fun UInt.withBit(bit: Int, value: Boolean) =
        if (value) {
            this.or(uintMasks[bit])
        } else {
            this.and(uintInvertedMasks[bit])
        }

@ExperimentalUnsignedTypes
fun UInt.bit(bit: Int): Boolean = this.and(uintMasks[bit]) == uintMasks[bit]

@ExperimentalUnsignedTypes
val UInt.bytes
    get() = arrayOf(
            byte(0),
            byte(1),
            byte(2),
            byte(3)
    )

@ExperimentalUnsignedTypes
fun UInt.byte(position: Int): UByte {
    val bitOffset = position * 8
    val mask = 0xFu shl bitOffset
    return ((this and mask) shr bitOffset).toUByte()
}


