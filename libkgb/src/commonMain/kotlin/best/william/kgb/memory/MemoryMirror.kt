package best.william.kgb.memory

import kgb.memory.IMemory
import kotlin.math.abs

class MemoryMirror(
    override val addressRange: UIntRange,
    private val mirroredIMemory: IMemory
): IMemory {
    private val rangeStart: UInt = addressRange.first
    private val mirrorOffset: Int
        get() {
            return mirroredIMemory.addressRange.first.toInt() - rangeStart.toInt()
        }

    private val mappedAddressRange: UIntRange
            =  (addressRange.start.toInt() + mirrorOffset).toUInt()..(addressRange.endInclusive.toInt() + mirrorOffset).toUInt()

    init {
        if (addressRange.isEmpty()) {
            throw IllegalArgumentException("Address range cannot be empty")
        }
        println("initialized with range start: ${rangeStart.toHexString()}, mirror offset: ${abs(mirrorOffset).toHexString()}")
        println("The mapped range is ${mappedAddressRange.start.toHexString()}..${mappedAddressRange.endInclusive.toHexString()}")
    }


    override fun set(position: UShort, value: UByte) {
        val mirroredPosition = (position.toInt() + mirrorOffset)
        mirroredIMemory[mirroredPosition.toUShort()] = value
    }

    override fun get(position: UShort): UByte {
        val mirroredPosition = (position.toInt() + mirrorOffset)
        return mirroredIMemory[mirroredPosition.toUShort()]
    }
}