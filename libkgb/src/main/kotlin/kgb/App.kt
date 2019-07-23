package best.william.kgb

import kgb.lcd.LCD
import kgb.memory.IORegisters
import kgb.memory.MemoryMapper
import kgb.memory.UByteArrayMemory
import kgb.rom.loadAsRom
import kgb.util.toAssemblyString
import java.io.File
import kotlin.system.measureNanoTime
import best.william.kgb.cpu.LR35902 as CPU

@ExperimentalUnsignedTypes
fun main() {
    val rom = File("reference/roms/tetris.gb").loadAsRom()
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
            println(buildString {
                append("PC: 0x${cpu.programCounter.toString(16)} ${memoryMap[cpu.programCounter].toAssemblyString()}")
                val time = measureNanoTime {
                    cpu.step()
                }
                append(" TIME: $time")
            })
            lcd.update(4u)
        }
    } catch (e: Exception) {
        println(e.printStackTrace())
        println(cpu)
    }
}