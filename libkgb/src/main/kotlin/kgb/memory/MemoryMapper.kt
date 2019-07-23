package kgb.memory

import best.william.kgb.memory.IMemory

@ExperimentalUnsignedTypes
class MemoryMapper(private vararg val memoryChunks: IMemory): IMemory {

    private val firstChunk = memoryChunks.minBy { it.addressRange.first }!!
    private val lastChunk = memoryChunks.maxBy { it.addressRange.last }!!

    override val addressRange: UIntRange = firstChunk.addressRange.first..lastChunk.addressRange.last

    override fun set(position: UShort, value: UByte) {
        val memory = memoryChunks.firstOrNull { it.addressRange.contains(position.toUInt()) } ?: return
        memory[position] = value
    }

    override fun get(position: UShort): UByte {
        val memory = memoryChunks.firstOrNull { it.addressRange.contains(position.toUInt()) } ?: return 0u
        return memory[position]
    }
}