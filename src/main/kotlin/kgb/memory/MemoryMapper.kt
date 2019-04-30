package kgb.memory

import best.william.kgb.memory.IMemory

@ExperimentalUnsignedTypes
class MemoryMapper(private vararg val memoryChunks: IMemory): IMemory {
    override val maxAddressSpace: UInt by lazy {
        memoryChunks.sumBy { it.maxAddressSpace.toInt() }.toUInt()
    }

    private val positions: List<UInt>

    init {
        val list = mutableListOf(0u)
        for (memory in memoryChunks) {
            list.add(list.last() + memory.maxAddressSpace)
        }
        positions = list
    }

    override fun set(position: UShort, value: UByte) {
        val index = positions.indexOfLast { position > it }.toUShort()
        val memory = memoryChunks[index.toInt()]
        memory[(position - positions[index.toInt()]).toUShort()] = value
    }

    override fun get(position: UShort): UByte {
        val index = positions.indexOfLast { position > it }
        val memory = memoryChunks[index]
        return memory[(position - positions[index]).toUShort()]
    }

}