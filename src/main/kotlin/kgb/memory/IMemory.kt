package best.william.kgb.memory


@ExperimentalUnsignedTypes
interface IMemory {
    val maxAddressSpace: UInt
    operator fun set(position: UShort, value: UByte)
    operator fun get(position: UShort): UByte
}