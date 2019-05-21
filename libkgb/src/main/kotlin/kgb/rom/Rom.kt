package kgb.rom

import best.william.kgb.memory.IMemory
import kgb.memory.UByteArrayMemory
import java.io.File

@ExperimentalUnsignedTypes
sealed class Rom(
        val name: String
): IMemory {
    override val maxAddressSpace: UInt
        get() = 0x8000u // Max addressable space by a cartridge
}

@ExperimentalUnsignedTypes
class RomOnly(
        bytes: ByteArray,
        name: String
): Rom(name) {
    private val rom = UByteArrayMemory(maxAddressSpace, bytes.toUByteArray())

    override fun set(position: UShort, value: UByte) {
        // READ ONLY
    }

    override fun get(position: UShort): UByte {
        return rom[position]
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
        return rom[position]
    }

    val selectedBank = 0
    val rom = UByteArrayMemory(maxAddressSpace, bytes.toUByteArray())
    val banks = bytes.toList()
            .chunked(0x8000)
            .map {
                UByteArrayMemory(it.size.toUInt(), it.toByteArray().toUByteArray())
            }
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

@ExperimentalUnsignedTypes
fun File.loadAsRom(): Rom {
    val bytes = this.readBytes()

    val romType = bytes[0x147]
    val romName = bytes.copyOfRange(0x134, 0x142)
            .map { it.toChar() }
            .joinToString("")
            .trim(0x0.toChar())

    return when (romType.toInt()) {
        0x0 -> RomOnly(bytes, romName)
        0xF -> MBC3(bytes, romName, timer = true, battery = true)
        0x10 -> MBC3(bytes, romName, ram = true, timer = true, battery = true)
        0x11 -> MBC3(bytes, romName)
        0x12 -> MBC3(bytes, romName, ram = true)
        0x13 -> MBC3(bytes, romName, ram = true, battery = true)
        else -> UnsupportedRomType
    }
}