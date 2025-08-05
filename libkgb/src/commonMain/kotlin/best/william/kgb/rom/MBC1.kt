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
    var bankingMode = 0 // 0 = ROM banking, 1 = RAM banking
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
                if (ramEnabled && ram) {
                    val ramBank = if (bankingMode == 0) 0 else ramBankNumber
                    ramBanks[ramBank % ramBanks.size][position.toInt() - 0xA000]
                } else {
                    0xFFu
                }
            }
            else -> throw IndexOutOfBoundsException("Position $position is out of bounds for MBC1")
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
                if (bankingMode == 0) {
                    rom.setHighBits(value.toUInt())
                } else {
                    ramBankNumber = value.toInt() // Set RAM bank number
                }
            }
            in 0x6000u..0x7FFFu -> {
                // Banking mode select
                bankingMode = value.toInt() and 0x01
            }
            in 0xA000u..0xBFFFu -> {
                if (ramEnabled && ram) {
                    val ramBank = if (bankingMode == 0) 0 else ramBankNumber
                    ramBanks[ramBank % ramBanks.size][position.toInt() - 0xA000] = value
                }
            }
        }
    }
}