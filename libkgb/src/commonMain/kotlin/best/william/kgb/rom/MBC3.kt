@file:OptIn(ExperimentalUnsignedTypes::class)

package kgb.rom

class MBC3(
        bytes: ByteArray,
        name: String,
        val timer: Boolean = false,
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
        println("Initialzied MBC3 with ${banks.size} ROM banks and ${ramBanks.size} RAM banks")
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
                rom.setSelectorLowerBits(value.toUInt(), 7)
            }
            in 0x4000u..0x5FFFu -> {
                if (value < 0x07u) {
                    ramBankNumber = value.toInt() // Set RAM bank number
                } else {
                    // RTC register selection
                    // This is not implemented in this example, but would typically select an RTC register
                    print("RTC register selection not implemented, value: $value")
                }
            }
            in 0x6000u..0x7FFFu -> {
                // Timer control, not implemented in this example
                // This would typically control the timer state and enable/disable it
                print("Timer control not implemented, value: $value")
            }
            in 0xA000u..0xBFFFu -> ramBanks[ramBankNumber][position.toInt() - 0xA000] = value
            else -> throw IndexOutOfBoundsException("Position $position is out of bounds for MBC3")
        }
    }
}