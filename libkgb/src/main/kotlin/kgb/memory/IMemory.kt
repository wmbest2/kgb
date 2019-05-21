package best.william.kgb.memory


@ExperimentalUnsignedTypes
interface IMemory {
    val maxAddressSpace: UInt
    operator fun set(position: UShort, value: UByte)
    operator fun get(position: UShort): UByte
}

operator fun IMemory.plus(iMemory: IMemory): MutableList<IMemory> {
    return mutableListOf(this, iMemory)
}

operator fun MutableList<IMemory>.plus(iMemory: IMemory): MutableList<IMemory> {
    this.add(iMemory)
    return this
}