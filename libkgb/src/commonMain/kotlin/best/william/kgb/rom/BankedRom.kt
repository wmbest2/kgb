package kgb.rom

import kgb.memory.IMemory

@ExperimentalUnsignedTypes
class BankedRom(
    chunks: List<UByteArray>,
): IMemory, RomBankSelector {
    override val addressRange: UIntRange = 0x0000u..0x7FFFu

    override val banks: List<UByteArray> = chunks
    override var selectedBank: Int = 1

    override fun get(position: UShort): UByte {
        if (position < 0x4000u) {
            // This is the first bank, which is always bank 0
            return banks[0][position.toInt()]
        } else {
            //println("Getting value at position ${position.toHexString()} in BankedRom, selected bank: $selectedBank")
            // This is a banked ROM, return the selected bank
            if (selectedBank < 0 || selectedBank >= banks.size) {
                throw IndexOutOfBoundsException("Selected bank $selectedBank is out of bounds for ${banks.size - 1} banks")
            }
            return currentBank()[(position - 0x4000u).toInt()]
        }
    }

    override fun set(position: UShort, value: UByte) {
        // Do Nothing for ROM writes
    }
}