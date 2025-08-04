@file:OptIn(ExperimentalUnsignedTypes::class)

package kgb.rom


import kgb.memory.IMemory
import kgb.memory.UByteArrayMemory

sealed class Cartridge(
    val name: String,
): CompoundMemory()

sealed interface Mode {
    object Rom : Mode
    object Ram : Mode
}

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
            if (selectedBank <= 0 || selectedBank >= banks.size - 1) {
                throw IndexOutOfBoundsException("Selected bank $selectedBank is out of bounds for ${banks.size - 1} banks")
            }
            return currentBank()[(position - 0x4000u).toInt()]
        }
    }

    override fun set(position: UShort, value: UByte) {
        // Do Nothing for ROM writes
    }
}

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

class MBC1(
        bytes: ByteArray,
        name: String,
        val ram: Boolean = false,
        val battery: Boolean = false
): Cartridge(name) {
    override val handledAddressRanges: List<UIntRange> = listOf(
        0x0000u..0x7FFFu,
        0xA000u..0xBFFFu  // RAM bank (if enabled)
    )

    val rom: IMemory
    var ramBank: IMemory? = null

    init {
        val banks: List<UByteArray> = bytes.toList()
                .chunked(0x4000)
                .map { it.toByteArray().toUByteArray() }

        rom = BankedRom(banks)

        if (ram) {
            ramBank = UByteArrayMemory(0xA000u..0xBFFFu, ByteArray(0x2000).toUByteArray())
        }
    }

    override fun get(position: UShort): UByte {
        return when {
            position in 0x0000u..0x7FFFu -> rom.get(position)
            position in 0xA000u..0xBFFFu && ramBank != null -> ramBank!!.get(position)
            else -> throw IndexOutOfBoundsException("Position $position is out of bounds for MBC1")
        }
    }

    override fun set(position: UShort, value: UByte) {
        when {
            position in 0x0000u..0x7FFFu -> rom.set(position, value)
            position in 0xA000u..0xBFFFu && ramBank != null -> ramBank!!.set(position, value)
            else -> throw IndexOutOfBoundsException("Position $position is out of bounds for MBC1")
        }
    }
}

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