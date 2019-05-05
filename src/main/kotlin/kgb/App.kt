package best.william.kgb

import kgb.memory.UByteArrayMemory
import java.io.File
import best.william.kgb.cpu.LR35902 as CPU

@ExperimentalUnsignedTypes
fun main() {
    val bytes = File("reference/DMG_ROM.bin").readBytes()
    val memory = UByteArrayMemory(0xFFFFu)
    for ((index, byte) in bytes.withIndex()) {
        memory[index.toUShort()] = byte.toUByte()
    }
    val cpu = CPU(memory)
    cpu.programCounter = 0x0u

    while (true) {
        cpu.step()
        if (cpu.programCounter == 0x1du.toUShort()) {
            continue
        }
    }
}