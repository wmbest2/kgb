@file:OptIn(ExperimentalUnsignedTypes::class)

package kgb.rom

import kgb.memory.IMemory
import kgb.memory.UByteArrayMemory

class MBC1(
        bytes: ByteArray,
        name: String,
        val ram: Boolean = false,
        val battery: Boolean = false
): Cartridge(name) {
    override val handledAddressRanges: List<UIntRange>
        get() = listOf(
            0x0000u..0x7FFFu,
            if (ram) 0xA000u..0xBFFFu else UIntRange.EMPTY
        )

    val rom: BankedRom
    var ramEnabled = false // RAM enable flag, set by writing to 0x0000-0x1FFF
    var ramBankNumber = 0
    val rtcEnabled: Boolean = false
    val rtcRegisterSelector = 0
    val rtcRegisters: UByteArray = UByteArray(5) // RTC registers (
    val ramBanks = List(4) { UByteArray(0x2000) } // 4 RAM banks of 8KB each

    init {
        val banks: List<UByteArray> = bytes.toList()
            .chunked(0x4000)
            .map { it.toByteArray().toUByteArray() }

        rom = BankedRom(banks)
        println("Initialzied MBC1 with ${banks.size} ROM banks and ${ramBanks.size} RAM banks")
    }

    override fun get(position: UShort): UByte {
        return when (position) {
            in 0x0000u..0x7FFFu -> rom[position]
            in 0xA000u..0xBFFFu -> {
                if (ramEnabled) {
                    ramBanks[ramBankNumber][position.toInt() - 0xA000] // Get from the selected RAM bank
                } else {
                    0xFFu // Return 0xFF if RAM is disabled
                }
            }
            else -> throw IndexOutOfBoundsException("Position $position is out of bounds for MBC3")
        }
    }

    override fun set(position: UShort, value: UByte) {
        when (position) {
            in 0x0000u..0x1FFFu -> {
                // RAM enable/disable
                if (value.toUInt() != 0x00u) {
                    ramEnabled = true // Enable RAM
                } else {
                    ramEnabled = false // Disable RAM
                }
            }
            in 0x2000u..0x3FFFu -> {
                rom.setSelectorLowerBits(
                    value = value.toUInt(),
                    numberOfBits = 5,
                    enableRomBankSelectionBug = true
                )
            }
            in 0x4000u..0x5FFFu -> {
                if (ramEnabled) {
                    ramBankNumber = value.toInt() // Set RAM bank number
                } else {
                    rom.setHighBits(value.toUInt())
                }
            }
            in 0x6000u..0x7FFFu -> {
                // MBC1 does not use this range, but we can handle it if needed
            }
            in 0xA000u..0xBFFFu -> ramBanks[ramBankNumber][position.toInt() - 0xA000] = value
            else -> throw IndexOutOfBoundsException("Position $position is out of bounds for MBC3")
        }
    }
}