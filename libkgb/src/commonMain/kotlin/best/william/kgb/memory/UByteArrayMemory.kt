package kgb.memory

import kgb.memory.IMemory
import kgb.util.debug

@ExperimentalUnsignedTypes
class UByteArrayMemory(
        override val addressRange: UIntRange,
        data: UByteArray = UByteArray(addressRange.count())
): IMemory {
    private val internalMemory = if (data.size != addressRange.count()) {
        data.copyOf(addressRange.count())
    } else {
        data
    }

    fun clear() {
        internalMemory.fill(0u)
    }

    override operator fun set(position: UShort, value: UByte) {
        internalMemory[(position - addressRange.start).toInt()] = value
    }

    override operator fun get(position: UShort): UByte {
        if (position !in addressRange) {
            throw IndexOutOfBoundsException("Position ${position.toHexString()} is out of bounds for UByteArrayMemory with range ${addressRange.debug}")
        }
        return internalMemory[(position - addressRange.start).toInt()]
    }
}
