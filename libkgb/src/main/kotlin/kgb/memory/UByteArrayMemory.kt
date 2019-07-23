package kgb.memory

import best.william.kgb.memory.IMemory

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

    override fun set(position: UShort, value: UByte) {
        internalMemory[(position - addressRange.first).toInt()] = value
    }

    override fun get(position: UShort): UByte {
        return internalMemory[(position - addressRange.first).toInt()]
    }
}
