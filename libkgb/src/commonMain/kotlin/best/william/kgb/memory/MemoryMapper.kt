package kgb.memory

@ExperimentalUnsignedTypes
class MemoryMapper(private vararg val _memoryChunks: IMemory): IMemory {

    private val memoryChunks = _memoryChunks.sortedBy { it.addressRange.first }

    private val firstChunk = memoryChunks.minByOrNull { it.addressRange.first }!!
    private val lastChunk = memoryChunks.maxByOrNull { it.addressRange.last }!!

    override val addressRange: UIntRange = firstChunk.addressRange.first..lastChunk.addressRange.last

    override fun set(position: UShort, value: UByte) {
        val memory = memoryChunks.find { it.addressRange.contains(position.toUInt()) } ?: return
        memory.set(position, value)
    }

    override fun get(position: UShort): UByte {
        val memory = memoryChunks.find { it.addressRange.contains(position.toUInt()) } ?: return 0u
        return memory.get(position)
    }
}