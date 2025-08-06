package kgb.memory

import best.william.kgb.audio.APU
import best.william.kgb.controller.Controller
import kgb.lcd.LCD

interface CPURegisters {
    var IF: UByte
    var IE: UByte

    var DIV: UByte
    var TIMA: UByte
    var TMA: UByte
    var TAC: UByte
}

class InterruptEnabledMemory(): IMemory {
    private var CPURegisters: CPURegisters? = null

    fun attachInterruptRegisters(ir: CPURegisters) {
        CPURegisters = ir
    }

    override val addressRange: UIntRange = 0xFFFFu..0xFFFFu // Only IE register
    override fun set(position: UShort, value: UByte) {
        if (position.toUInt() == 0xFFFFu) {
            println("Setting IE register at position ${position.toHexString()} to value ${value.toHexString() })}")
            CPURegisters?.IE = value
        }
    }
    override fun get(position: UShort): UByte {
        return if (position.toUInt() == 0xFFFFu) {
            val output = CPURegisters?.IE ?: 0u
            println("Getting IE register at position ${position.toHexString()} with value ${output.toHexString() })}")
            output
        } else {
            0u // Invalid position, return 0
        }
    }
}

@ExperimentalUnsignedTypes
class IORegisters: IMemory {
    // LCD and interrupt registers are attached after construction
    private var lcd: LCD? = null
    private var CPURegisters: CPURegisters? = null
    private lateinit var controller: Controller
    lateinit var apu: APU

    fun attachLCD(lcd: LCD) {
        this.lcd = lcd
    }
    fun attachCPURegisters(ir: CPURegisters) {
        CPURegisters = ir
    }
    fun attachController(controller: Controller) {
        this.controller = controller
    }
    // Backing fields for registers
    var JOYP: UByte
        get() = controller.JOYP
        set(value) { controller.JOYP = value }
    var SB: UByte = 0x00u    // Serial transfer data register
    var SC: UByte = 0x7Eu    // Serial I/O control register
    var BANK: UByte = 0u
    var DIV: UByte
        get() = CPURegisters?.DIV ?: 0u
        set(value) { CPURegisters?.DIV = value }
    var TIMA: UByte
        get() = CPURegisters?.TIMA ?: 0u
        set(value) { CPURegisters?.TIMA = value }
    var TMA: UByte
        get() = CPURegisters?.TMA ?: 0u
        set(value) { CPURegisters?.TMA = value }
    var TAC: UByte
        get() = CPURegisters?.TAC ?: 0u
        set(value) { CPURegisters?.TAC = value }

    var IF: UByte
        get() = CPURegisters?.IF ?: 0u
        set(value) { CPURegisters?.IF = value }

    override val addressRange: UIntRange = 0xFF00u..0xFF7Fu

    override fun set(position: UShort, value: UByte) = when(position.toUInt()) {
        0xFF00u -> JOYP = value
        0xFF01u -> SB = value
        0xFF02u -> SC = value
        0xFF04u -> DIV = 0u // Writing resets DIV
        0xFF05u -> TIMA = value
        0xFF06u -> TMA = value
        0xFF07u -> TAC = value
        0xFF0Fu -> IF = value

        0xFF10u -> apu.NR10 = value
        0xFF11u -> apu.NR11 = value
        0xFF12u -> apu.NR12 = value
        0xFF13u -> apu.NR13 = value
        0xFF14u -> apu.NR14 = value
        0xFF16u -> apu.NR21 = value
        0xFF17u -> apu.NR22 = value
        0xFF18u -> apu.NR23 = value
        0xFF19u -> apu.NR24 = value
        0xFF1Au -> apu.NR30 = value
        0xFF1Bu -> apu.NR31 = value
        0xFF1Cu -> apu.NR32 = value
        0xFF1Du -> apu.NR33 = value
        0xFF1Eu -> apu.NR34 = value
        0xFF20u -> apu.NR41 = value
        0xFF21u -> apu.NR42 = value
        0xFF22u -> apu.NR43 = value
        0xFF23u -> apu.NR44 = value
        0xFF24u -> apu.NR50 = value
        0xFF25u -> apu.NR51 = value
        0xFF26u -> apu.NR52 = value and 0x80u

        // Audio Register
        in 0xFF30u..0xFF3Fu -> {
            // Writing to wave RAM, which is used for sound
            apu.waveRam[(position - 0xFF30u).toInt()] = value
        }

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

        // Audio Registers
        0xFF10u -> apu.NR10
        0xFF11u -> apu.NR11
        0xFF12u -> apu.NR12
        0xFF13u -> apu.NR13
        0xFF14u -> apu.NR14
        0xFF16u -> apu.NR21
        0xFF17u -> apu.NR22
        0xFF18u -> apu.NR23
        0xFF19u -> apu.NR24
        0xFF1Au -> apu.NR30
        0xFF1Bu -> apu.NR31
        0xFF1Cu -> apu.NR32
        0xFF1Du -> apu.NR33
        0xFF1Eu -> apu.NR34
        0xFF20u -> apu.NR41
        0xFF21u -> apu.NR42
        0xFF22u -> apu.NR43
        0xFF23u -> apu.NR44
        0xFF24u -> apu.NR50
        0xFF25u -> apu.NR51
        0xFF26u -> apu.NR52
        in 0xFF30u..0xFF3Fu-> {
            // Reading from wave RAM, which is used for sound
            apu.waveRam[(position - 0xFF30u).toInt()]
        }

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
        else -> 0u
    }

}