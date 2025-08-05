package kgb.rom

import kgb.memory.UByteArrayMemory

class RomOnly(
        bytes: ByteArray,
        name: String
): Cartridge(name) {
    val rom = UByteArrayMemory(0x0000u..0x7FFFu, bytes.toUByteArray())
    override val handledAddressRanges: List<UIntRange> = listOf(rom.addressRange)

    override fun get(position: UShort): UByte {
        if (position < 0x0000u || position > 0x7FFFu) {
            throw IndexOutOfBoundsException("Position $position is out of bounds for RomOnly")
        }
        return rom[position]
    }

    override fun set(position: UShort, value: UByte) {}
}