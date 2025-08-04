package kgb.rom

import kgb.memory.IMemory

abstract class CompoundMemory {
    abstract val handledAddressRanges: List<UIntRange>
    fun getMemoryChunks(): Array<out IMemory> {
        return handledAddressRanges.filter {
            !it.isEmpty()
        }.map { addressRange ->
            DelegateMemory(addressRange, this)
        }.toTypedArray()
    }

    abstract fun get(position: UShort): UByte
    abstract fun set(position: UShort, value: UByte)

    class DelegateMemory(
        override val addressRange: UIntRange,
        private val delegate: CompoundMemory
    ): IMemory {
        override fun set(position: UShort, value: UByte) {
            //println("Setting value $value at position ${position.toHexString()} in DelegateMemory")
            delegate.set(position, value)
        }

        override fun get(position: UShort): UByte {
            return delegate.get(position)
        }
    }
}