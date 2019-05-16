package kgb.rom

import best.william.kgb.memory.IMemory
import java.io.File

@ExperimentalUnsignedTypes
sealed class Rom(
        val name: String
): IMemory

class RomOnly(val name: String)

@ExperimentalUnsignedTypes
fun File.loadAsRom() {
    val bytes = this.readBytes()

    val romType = bytes[0x147].toString(16)
    val romName = bytes.copyOfRange(0x134, 0x142)
            .map { it.toChar() }
            .joinToString("")
            .trim(0x0.toChar())

    println("Rom Type: $romType")
    println("Rom Name: $romName")
}