package kgb.memory

import best.william.kgb.memory.IMemory

@ExperimentalUnsignedTypes
class GBMemoryMap (
        private val mbc: IMemory
): IMemory {
    override val maxAddressSpace = 0x10000u

    override fun set(position: UShort, value: UByte) {
        val memory = when {
            position < 0xBFFFu -> mbc
            else -> null
        }
        memory?.set(position, value)
    }

    override fun get(position: UShort): UByte {
        val memory = when {
            position < 0xBFFFu -> mbc
            else -> null
        }
        return memory?.get(position) ?: 0x0u
    }

}