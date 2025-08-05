@file:OptIn(ExperimentalUnsignedTypes::class)

package best.william.kgb

import best.william.kgb.controller.Controller
import best.william.kgb.cpu.LR35902 as CPU
import best.william.kgb.memory.MemoryMirror
import kgb.lcd.LCD
import kgb.lcd.LCDRenderer
import kgb.memory.IORegisters
import kgb.memory.InterruptEnabledMemory
import kgb.memory.MemoryMapper
import kgb.memory.UByteArrayMemory
import kgb.rom.Cartridge

class Gameboy(
    bootRom: ByteArray,
    renderer: LCDRenderer,
    controller: Controller
) {
    val _bootRom = UByteArrayMemory(0x0000u..0x00FFu, bootRom.toUByteArray())
    val vram = UByteArrayMemory(0x8000u..0x9FFFu)
    val wram = UByteArrayMemory(0xC000u..0xDFFFu)
    val echoRam = MemoryMirror(0xE000u..0xFDFFu, wram)

    // Unused memory 0xFF80 to 0xFFFE
    // This is typically used for high RAM (HRAM) and is not directly accessible by the CPU,
    // but can be used for temporary storage or flags.
    val hram = UByteArrayMemory(0xFF80u..0xFFFEu)

    // I/O Registers 0xFF00 to 0xFF7F
    val ioRegisters = IORegisters()
    val interruptEnabledMemory = InterruptEnabledMemory()

    val cpu = CPU()
    val lcd = LCD(renderer)

    val memoryMap = MemoryMapper(
        _bootRom, // Boot ROM is usually only used for the first few cycles
        vram, // Video RAM 0x8000 to 0x9FFF
        wram,
        echoRam,
        lcd.oam,
        ioRegisters,
        hram,
        interruptEnabledMemory
    )

    init {
        cpu.memory = memoryMap
        lcd.memory = memoryMap
        controller.interruptProvider = cpu


        interruptEnabledMemory.attachInterruptRegisters(cpu)
        ioRegisters.attachCPURegisters(cpu)
        ioRegisters.attachController(controller)
        ioRegisters.attachLCD(lcd)

        lcd.interruptProvider = cpu
    }

    fun loadCatridge(cartridge: Cartridge) {
        memoryMap.cartridge = cartridge
    }

    fun reset() {
        cpu.reset()
        lcd.reset()
        memoryMap.cartridge = null
        vram.clear()
        wram.clear()
        hram.clear()
    }

    fun update(): Boolean {
        // Emulate CPU cycles and update LCD
        val cycles = cpu.step()
        return lcd.update(cycles)
    }

    override fun toString(): String {
        return "Gameboy(cpu=$cpu, lcd=$lcd, memoryMap=$memoryMap)"
    }
}