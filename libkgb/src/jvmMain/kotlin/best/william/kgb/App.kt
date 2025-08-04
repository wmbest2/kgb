package best.william.kgb

import best.william.kgb.memory.MemoryMirror
import best.william.kgb.rom.loadAsRom
import kgb.lcd.LCD
import kgb.memory.IORegisters
import kgb.memory.InterruptEnabledMemory
import kgb.memory.MemoryMapper
import kgb.memory.UByteArrayMemory
import java.io.File
import kotlin.time.ExperimentalTime
import best.william.kgb.cpu.LR35902 as CPU

@ExperimentalTime
@ExperimentalUnsignedTypes
fun main() {
    val bootRomBytes = File("../reference/DMG_ROM.bin").readBytes().toUByteArray()
    val bootRom = UByteArrayMemory(0x0000u..0x00FFu, bootRomBytes)
    // Rom range: 0x0100 to 0x7FFF
    // Optional Rom Bank 0x8000 to 0x9FFF
    //val rom = File("../reference/roms/test/cpu_instrs/individual/02-interrupts.gb").loadAsRom()
    //val rom = File("../reference/roms/test/oam_bug/rom_singles/1-lcd_sync.gb").loadAsRom()
    //val rom = File("../reference/roms/test/cpu_instrs.gb").loadAsRom()
    val rom = File("../reference/roms/tetris.gb").loadAsRom()

    val vram = UByteArrayMemory(0x8000u..0x9FFFu)
    // Optional Switchable RAM Bank 0xA000 to 0xBFFF
    val ram = UByteArrayMemory(0xC000u..0xDFFFu)
    val oam = UByteArrayMemory(0xFE00u..0xFE9Fu) // OAM memory for sprites

    // I/O Registers 0xFF00 to 0xFF7F
    val ioRegisters = IORegisters()

    // Unused memory 0xFF80 to 0xFFFE
    // This is typically used for high RAM (HRAM) and is not directly accessible by the CPU,
    // but can be used for temporary storage or flags.
    val hram = UByteArrayMemory(0xFF80u..0xFFFEu)
    val interruptEnabledMemory = InterruptEnabledMemory()

    val memoryMap = MemoryMapper(
        bootRom, // Boot ROM is usually only used for the first few cycles
        rom, // Main ROM 0x0000 to 0x7FFF
        vram, // Video RAM 0x8000 to 0x9FFF
        ram,
        MemoryMirror(0xE000u..0xFDFFu, ram),
        oam,
        ioRegisters,
        hram,
        interruptEnabledMemory
    )

    val cpu = CPU(memoryMap)
    val renderer = best.william.kgb.lcd.LWJGLRenderer(cpu)
    val lcd = LCD(memoryMap, renderer)


    ioRegisters.attachCPURegisters(cpu)
    interruptEnabledMemory.attachInterruptRegisters(cpu)
    ioRegisters.attachController(renderer)
    ioRegisters.attachLCD(lcd)

    lcd.interruptProvider = cpu

    println("Running ${rom.name}")

    try {
        while (!renderer.shouldClose()) {
            // Emulate CPU cycles and update LCD
            val cycles = cpu.step()
            lcd.update(cycles)
        }
    } catch (e: Exception) {
        println(e.printStackTrace())
        println(cpu)
    } finally {
        renderer.dispose()
    }
}
