@file:OptIn(ExperimentalUnsignedTypes::class)

package kgb.rom


import kgb.memory.IMemory
import kgb.memory.UByteArrayMemory

sealed class Cartridge(
    val name: String,
): CompoundMemory()

@ExperimentalUnsignedTypes
object UnsupportedRomType: Cartridge("UNSUPPORTED") {
    val rom: IMemory = UByteArrayMemory(0x0000u..0x0000u)

    override val handledAddressRanges: List<UIntRange> = listOf(rom.addressRange)
    override fun get(position: UShort): UByte {
        throw UnsupportedOperationException("Unsupported ROM type")
    }
    override fun set(position: UShort, value: UByte) {
        throw UnsupportedOperationException("Unsupported ROM type")
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
fun ByteArray.loadCartridge(): Cartridge {

    val romType = this[0x147]
    val romName = this.copyOfRange(0x134, 0x142)
        .map { it.toInt().toChar() }
        .joinToString("")
        .trim(0x0.toChar())

    return when (romType.toInt()) {
        0x0 -> RomOnly(this, romName)
        0x1 -> MBC1(this, romName)
        0x2 -> MBC1(this, romName, ram = true)
        0x3 -> MBC1(this, romName, ram = true, battery = true)
        0xF -> MBC3(this, romName, timer = true, battery = true)
        0x10 -> MBC3(this, romName, ram = true, timer = true, battery = true)
        0x11 -> MBC3(this, romName)
        0x12 -> MBC3(this, romName, ram = true)
        0x13 -> MBC3(this, romName, ram = true, battery = true)
        else -> UnsupportedRomType
    }
}