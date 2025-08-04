package kgb.rom

interface RomBankSelector {
    var selectedBank: Int

    val banks: List<UByteArray>

    fun currentBank(): UByteArray {
        if (selectedBank < 0 || selectedBank >= banks.size) {
            throw IndexOutOfBoundsException("Selected bank $selectedBank is out of bounds for ${banks.size} banks")
        }
        return banks[selectedBank]
    }

    fun setSelectorLowerBits(value: UInt, numberOfBits: Int, enableRomBankSelectionBug: Boolean = false) {
        // ROM bank selection (0x2000-0x3FFF)
        val mask = (1u shl numberOfBits) - 1u // Create a mask for the lower bits
        val invertedMask = mask.inv() // Invert the mask to keep upper bits unchanged

        // Keep top 8-n bits and set the lower n bits
        var bank = ((selectedBank.toUInt() and invertedMask) or (value and mask)).toInt() //
        //MBC1 ROM bank selection bug handling
        if ((enableRomBankSelectionBug && value == 0u) || selectedBank == 0 ) {
            bank = 1 // Bank 0 maps to bank 1 in the switchable area
        }

        selectedBank = bank
    }

    fun setHighBits(value: UInt) {
        // ROM bank selection (0x4000-0x5FFF)
        // This is the upper 2 bits of the bank number
        val previousBottomBits = selectedBank.toUInt() and 0x1Fu // Keep the lower 5 bits unchanged
        selectedBank = (previousBottomBits or ((value and 0x03u) shl 5)).toInt()
    }
}