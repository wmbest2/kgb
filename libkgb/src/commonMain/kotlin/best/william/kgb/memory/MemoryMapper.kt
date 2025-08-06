package best.william.kgb.memory

import kgb.memory.IMemory
import kgb.rom.Cartridge

@ExperimentalUnsignedTypes
class MemoryMapper(
    private val boot: IMemory,
    private vararg val _memoryChunks: IMemory
): IMemory {

    private var bootEnabled = true

    private var fullMemoryMap: Array<IMemory> = emptyArray()
        set(value) {
            field = value
            memoryMapSize = field.size
        }

    private var memoryMapSize: Int = 0

    var cartridge: Cartridge? = null
        set(value) {
            field = value
            val cartridgeChunks = value?.getMemoryChunks()?.sortedBy { it.addressRange.first } ?: emptyList()
            fullMemoryMap = (cartridgeChunks + _memoryChunks).sortedBy { it.addressRange.first }.toTypedArray()
        }

    override var addressRange: UIntRange = 0x0000u..0xFFFFu
        private set

    inline private fun findMemoryChunk(position: UShort): IMemory? {
        val chunks = fullMemoryMap
        var low = 0
        var high = memoryMapSize - 1
        while (low <= high) {
            val mid = (low + high) ushr 1
            val chunk = chunks[mid]
            if (position < chunk.addressRange.first) {
                high = mid - 1
            } else if (position > chunk.addressRange.last) {
                low = mid + 1
            } else {
                return chunk
            }
        }
        return null
    }

    override fun set(position: UShort, value: UByte) {
        if (bootEnabled && position < 0x0100u.toUShort()) {
            boot.set(position, value)
            return
        }
        val memory = findMemoryChunk(position) ?: return
        memory.set(position, value)

        // Disable boot ROM after the write to 0xFF50 has been processed
        if (bootEnabled && position == 0xFF50u.toUShort()) bootEnabled = false
    }


    override fun get(position: UShort): UByte {
        if (bootEnabled && position < 0x0100u.toUShort()) return boot[position] // Boot ROM disabled
        val memory = findMemoryChunk(position) ?: return 0u
        return memory.get(position)
    }
}