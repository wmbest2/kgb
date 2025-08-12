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
            return currentBank()[(position - 0x4000u).toInt()]
        }
    }

    override fun set(position: UShort, value: UByte) {
        // Do Nothing for ROM writes
    }
}