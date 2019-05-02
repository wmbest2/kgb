package kgb.memory

import best.william.kgb.memory.IMemory

@ExperimentalUnsignedTypes
class UByteArrayMemory(
        override val maxAddressSpace: UInt,
        private val internalMemory: UByteArray = UByteArray(maxAddressSpace.toInt())
): IMemory {

    override fun set(position: UShort, value: UByte) {
        internalMemory[position.toInt()] = value
    }

    override fun get(position: UShort): UByte {
        return internalMemory[position.toInt()]
    }
}
