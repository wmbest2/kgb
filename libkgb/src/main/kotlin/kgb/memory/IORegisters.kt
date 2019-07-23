package kgb.memory

import best.william.kgb.memory.IMemory
import kgb.lcd.LCD

@ExperimentalUnsignedTypes
class IORegisters(private val lcd: LCD): IMemory {
    override val addressRange: UIntRange = 0xFF00u..0xFF7Fu

    override fun set(position: UShort, value: UByte) = when(position.toUInt()) {
        0xFF40u -> lcd.LCDC = value
        0xFF41u -> lcd.STAT = value
        0xFF42u -> lcd.scrollX = value
        0xFF43u -> lcd.scrollY = value
        0xFF45u -> lcd.LYC = value
        else -> {}
    }

    override fun get(position: UShort) = when(position.toUInt()) {
        0xFF40u -> lcd.LCDC
        0xFF41u -> lcd.STAT
        0xFF42u -> lcd.scrollX
        0xFF43u -> lcd.scrollY
        0xFF44u -> lcd.LY
        0xFF45u -> lcd.LYC
        else -> 0u
    }

}