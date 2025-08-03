package kgb.memory

import kgb.lcd.LCD

interface InterruptRegisters {
    var IF: UByte
    var IE: UByte
}

class InterruptEnabledMemory(): IMemory {
    private var interruptRegisters: InterruptRegisters? = null

    fun attachInterruptRegisters(ir: InterruptRegisters) {
        interruptRegisters = ir
    }

    override val addressRange: UIntRange
        inline get() = 0xFFFFu..0xFFFFu // Only IE register
    override fun set(position: UShort, value: UByte) {
        if (position.toUInt() == 0xFFFFu) {
            interruptRegisters?.IE = value
        }
    }
    override fun get(position: UShort): UByte {
        return if (position.toUInt() == 0xFFFFu) {
            interruptRegisters?.IE ?: 0u
        } else {
            0u // Invalid position, return 0
        }
    }
}

@ExperimentalUnsignedTypes
class IORegisters: IMemory {
    // LCD and interrupt registers are attached after construction
    private var lcd: LCD? = null
    private var interruptRegisters: InterruptRegisters? = null

    fun attachLCD(lcd: LCD) {
        this.lcd = lcd
    }
    fun attachInterruptRegisters(ir: InterruptRegisters) {
        interruptRegisters = ir
    }
    // Backing fields for registers
    var JOYP: UByte = 0u
    var SB: UByte = 0u
    var SC: UByte = 0u
    var DIV: UByte = 0u
    var TIMA: UByte = 0u
    var TMA: UByte = 0u
    var TAC: UByte = 0u
    var BANK: UByte = 0u
    var IF: UByte
        get() = interruptRegisters?.IF ?: 0u
        set(value) { interruptRegisters?.IF = value }

    override val addressRange: UIntRange
        inline get() = 0xFF00u..0xFF7Fu

    override fun set(position: UShort, value: UByte) = when(position.toUInt()) {
        0xFF00u -> JOYP = value
        0xFF01u -> SB = value
        0xFF02u -> SC = value
        0xFF04u -> DIV = 0u // Writing resets DIV
        0xFF05u -> TIMA = value
        0xFF06u -> TMA = value
        0xFF07u -> TAC = value
        0xFF0Fu -> IF = value
        // LCD registers
        0xFF40u -> lcd?.LCDC = value
        0xFF41u -> lcd?.STAT = value
        0xFF42u -> lcd?.scrollY = value
        0xFF43u -> lcd?.scrollX = value
        0xFF44u -> lcd?.LY = value
        0xFF45u -> lcd?.LYC = value
        0xFF46u -> lcd?.DMA = value
        0xFF47u -> lcd?.BGP = value
        0xFF48u -> lcd?.OBP0 = value
        0xFF49u -> lcd?.OBP1 = value
        0xFF4Au -> lcd?.WY = value
        0xFF4Bu -> lcd?.WX = value
        0xFF50u -> BANK = value
        // Sound registers and others can be stubbed
        in 0xFF10u..0xFF3Fu -> {} // Sound stub
        else -> {}
    }

    override fun get(position: UShort) = when(position.toUInt()) {
        0xFF00u -> JOYP
        0xFF01u -> SB
        0xFF02u -> SC
        0xFF04u -> DIV
        0xFF05u -> TIMA
        0xFF06u -> TMA
        0xFF07u -> TAC
        0xFF0Fu -> IF
        // LCD registers
        0xFF40u -> lcd?.LCDC ?: 0u
        0xFF41u -> lcd?.STAT ?: 0u
        0xFF42u -> lcd?.scrollY ?: 0u
        0xFF43u -> lcd?.scrollX ?: 0u
        0xFF44u -> lcd?.LY ?: 0u
        0xFF45u -> lcd?.LYC ?: 0u
        0xFF46u -> lcd?.DMA ?: 0u
        0xFF47u -> lcd?.BGP ?: 0u
        0xFF48u -> lcd?.OBP0 ?: 0u
        0xFF49u -> lcd?.OBP1 ?: 0u
        0xFF4Au -> lcd?.WY ?: 0u
        0xFF4Bu -> lcd?.WX ?: 0u
        0xFF50u -> BANK // This is the boot ROM disable register
        // Sound registers and others can be stubbed
        in 0xFF10u..0xFF3Fu -> 0u // Sound stub
        else -> 0u
    }

}