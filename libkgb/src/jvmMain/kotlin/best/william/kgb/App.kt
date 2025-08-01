package best.william.kgb

import best.william.kgb.rom.loadAsRom
import kgb.lcd.LCD
import kgb.memory.IORegisters
import kgb.memory.MemoryMapper
import kgb.memory.UByteArrayMemory
import java.io.File
import kotlin.time.ExperimentalTime
import best.william.kgb.cpu.LR35902 as CPU

@ExperimentalTime
@ExperimentalUnsignedTypes
fun main() {
    val rom = File("../reference/roms/tetris.gb").loadAsRom()
    val lcd = LCD {

    }
    val ioRegisters = IORegisters(lcd)
    val ram = UByteArrayMemory(0xC000u..0xDFFFu)
    val memoryMap = MemoryMapper(rom, ram, ioRegisters)
//    val memoryMap = GBMemoryMap(rom)
    val cpu = CPU(memoryMap)
    lcd.interruptProvider = cpu
    cpu.programCounter = 0x100u

    println("Running ${rom.name}")

    try {
        while (true) {
            cpu.step(true)
            lcd.update(4u)
        }
    } catch (e: Exception) {
        println(e.printStackTrace())
        println(cpu)
    }
}