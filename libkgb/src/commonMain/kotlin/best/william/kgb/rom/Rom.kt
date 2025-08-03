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

    override val addressRange: UIntRange = 0x0000u..0x7FFFu
    override fun set(position: UShort, value: UByte) {
        // ROMs are read-only, so this should not be called
    }
}

class RomOnly(
        bytes: ByteArray,
        name: String
): Rom(name) {
    private val rom = UByteArrayMemory(addressRange, bytes.toUByteArray())

    override fun get(position: UShort): UByte {
        return rom[position]
    }
}

class MBC1(
        bytes: ByteArray,
        name: String,
        val ram: Boolean = false,
        val battery: Boolean = false
): Rom(name) {

    override fun get(position: UShort): UByte {
        if (position < 0x8000u) {
            return rom[position]
        }
        if (position in 0xA000u..0xBFFFu && ram) {
            return banks[selectedBank][(position - 0xA000u).toInt()].toUByte()
        }
        throw IllegalArgumentException("Invalid read from ROM: $name at position $position")
    }

    val selectedBank = 0
    val rom = UByteArrayMemory(addressRange, bytes.toUByteArray())
    val banks = bytes.toList()
            .chunked(0x8000)
            .map { it.toByteArray() }

    val bankMemory: IMemory
        get() = object : IMemory {
            override val addressRange: UIntRange
                get() = 0xA000u..0xBFFFu

            override fun set(position: UShort, value: UByte) {
                if (position in 0xA000u..0xBFFFu && ram) {
                    banks[selectedBank][(position - 0xA000u).toInt()] = value.toInt().toByte()
                } else {
                    throw IllegalArgumentException("Invalid write to ROM: $name at position $position")
                }
            }

            override fun get(position: UShort): UByte {
                return if (position in 0xA000u..0xBFFFu && ram) {
                    banks[selectedBank][(position - 0xA000u).toInt()].toUByte()
                } else {
                    throw IllegalArgumentException("Invalid read from ROM: $name at position $position")
                }
            }

        }
}

@ExperimentalUnsignedTypes
class MBC3(
        bytes: ByteArray,
        name: String,
        val ram: Boolean = false,
        timer: Boolean = false,
        battery: Boolean = false
): Rom(name) {
    override fun get(position: UShort): UByte {
        if (position < 0x8000u) {
            return rom[position]
        }
        if (position in 0xA000u..0xBFFFu && ram) {
            return banks[selectedBank][(position - 0xA000u).toInt()].toUByte()
        }
        throw IllegalArgumentException("Invalid read from ROM: $name at position $position")
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