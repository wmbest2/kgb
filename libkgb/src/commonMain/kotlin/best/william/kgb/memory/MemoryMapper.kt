package kgb.memory

@ExperimentalUnsignedTypes
class MemoryMapper(
    private val boot: IMemory,
    vararg _memoryChunks: IMemory
): IMemory {

    private var bootEnabled = true

    private val memoryChunks = _memoryChunks.sortedBy { it.addressRange.first }

    private val firstChunk = memoryChunks.minByOrNull { it.addressRange.first }!!
    private val lastChunk = memoryChunks.maxByOrNull { it.addressRange.last }!!

    override val addressRange: UIntRange = firstChunk.addressRange.first..lastChunk.addressRange.last

    override fun set(position: UShort, value: UByte) {
        if (bootEnabled && position < 0x0100u.toUShort()) {
            boot.set(position, value)
            return
        }
        val memory = memoryChunks.find { it.addressRange.contains(position.toUInt()) } ?: return
        memory.set(position, value)

        // Disable boot ROM after the write to 0xFF50 has been processed
        if (bootEnabled && position == 0xFF50u.toUShort()) bootEnabled = false
    }

    override fun get(position: UShort): UByte {
        if (bootEnabled && position < 0x0100u.toUShort()) return boot[position] // Boot ROM disabled
        val memory = memoryChunks.find { it.addressRange.contains(position.toUInt()) } ?: return 0u
        return memory.get(position)
    }
}