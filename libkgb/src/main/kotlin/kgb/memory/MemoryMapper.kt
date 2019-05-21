package kgb.memory

import best.william.kgb.memory.IMemory

@ExperimentalUnsignedTypes
class MemoryMapper(private vararg val memoryChunks: IMemory): IMemory {

    constructor(memoryChunks: List<IMemory>): this(memoryChunks = *memoryChunks.toTypedArray())

    override val maxAddressSpace: UInt = memoryChunks.sumBy {
        it.maxAddressSpace.toInt()
    }.toUInt()

    private val positions: UIntArray

    init {
        val list = mutableListOf(0u)
        var last = 0u
        for (memory in memoryChunks) {
            list.add(last + memory.maxAddressSpace)
            last = memory.maxAddressSpace
        }
        positions = list.toUIntArray()
    }

    override fun set(position: UShort, value: UByte) {
        val index = positions.indexOfLast { position > it }
        val memory = memoryChunks[index]
        memory[(position - positions[index]).toUShort()] = value
    }

    override fun get(position: UShort): UByte {
        val index = positions.indexOfLast { position > it }
        val memory = memoryChunks[index]
        return memory[(position - positions[index]).toUShort()]
    }
}