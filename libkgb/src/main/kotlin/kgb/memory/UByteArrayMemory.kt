package kgb.memory

import best.william.kgb.memory.IMemory

@ExperimentalUnsignedTypes
class UByteArrayMemory(
        override val maxAddressSpace: UInt,
        data: UByteArray = UByteArray(maxAddressSpace.toInt())
): IMemory {
    private val internalMemory = data.copyOf(maxAddressSpace.toInt())

    override fun set(position: UShort, value: UByte) {
        internalMemory[position.toInt()] = value
    }

    override fun get(position: UShort): UByte {
        return internalMemory[position.toInt()]
    }
}
