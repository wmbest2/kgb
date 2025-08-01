package kgb.rom

import kgb.memory.IMemory
import kgb.memory.UByteArrayMemory

interface Cartridge {
    val rom: IMemory
    val ram: IMemory
}

@ExperimentalUnsignedTypes
sealed class Rom(
        val name: String
): IMemory {
    override val addressRange: UIntRange = 0x0u..0x7FFFu
}

@ExperimentalUnsignedTypes
class RomOnly(
        bytes: ByteArray,
        name: String
): Rom(name) {
    private val rom = UByteArrayMemory(addressRange, bytes.toUByteArray())

    override fun set(position: UShort, value: UByte) {
        // READ ONLY
    }

    override fun get(position: UShort): UByte {
        return rom.get(position)
    }
}

@ExperimentalUnsignedTypes
class MBC3(
        bytes: ByteArray,
        name: String,
        ram: Boolean = false,
        timer: Boolean = false,
        battery: Boolean = false
): Rom(name) {
    override fun set(position: UShort, value: UByte) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(position: UShort): UByte {
        return rom.get(position)
    }

    val selectedBank = 0
    val rom = UByteArrayMemory(addressRange, bytes.toUByteArray())
    val banks = bytes.toList()
            .chunked(0x8000)
}

@ExperimentalUnsignedTypes
object UnsupportedRomType: Rom("UNSUPPORTED") {
    override fun set(position: UShort, value: UByte) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(position: UShort): UByte {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
fun ByteArray.toRom(): Rom {

    val romType = this[0x147]
    val romName = this.copyOfRange(0x134, 0x142)
        .map { it.toInt().toChar() }
        .joinToString("")
        .trim(0x0.toChar())

    return when (romType.toInt()) {
        0x0 -> RomOnly(this, romName)
        0xF -> MBC3(this, romName, timer = true, battery = true)
        0x10 -> MBC3(this, romName, ram = true, timer = true, battery = true)
        0x11 -> MBC3(this, romName)
        0x12 -> MBC3(this, romName, ram = true)
        0x13 -> MBC3(this, romName, ram = true, battery = true)
        else -> UnsupportedRomType
    }
}