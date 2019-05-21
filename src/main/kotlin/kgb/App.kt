package best.william.kgb

import best.william.kgb.memory.plus
import kgb.memory.GBMemoryMap
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
    val memoryMap = MemoryMapper(rom + UByteArrayMemory(0x8000u))
//    val memoryMap = GBMemoryMap(rom)
    val cpu = CPU(memoryMap)
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
        }
    } catch (e: Exception) {
        println(e.printStackTrace())
        println(cpu)
    }
}