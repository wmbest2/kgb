package best.william.kgb

import kgb.memory.UByteArrayMemory
import kgb.rom.loadAsRom
import kgb.util.debugCurrentOperation
import kgb.util.toAssemblyString
import java.io.File
import best.william.kgb.cpu.LR35902 as CPU

@ExperimentalUnsignedTypes
fun main() {
//    for (file in File("reference/roms/").listFiles()) {
//        file.loadAsRom()
//    }
    val bytes = File("reference/roms/Tetris (W) (V1.1) [!].gb").readBytes()
    val memory = UByteArrayMemory(0x10000u) // supports up to 0xFFFF addressable space
    for ((index, byte) in bytes.withIndex()) {
        memory[index.toUShort()] = byte.toUByte()
    }
    val cpu = CPU(memory)
    cpu.programCounter = 0x100u

    var run = true
    while (run) {
        try {
            cpu.debugCurrentOperation()
            cpu.step()
        } catch (e: Exception) {
            run = false
            println(e.printStackTrace())
            println(cpu)
        }
    }
}