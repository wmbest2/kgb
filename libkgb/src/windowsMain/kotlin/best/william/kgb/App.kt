@file:OptIn(ExperimentalForeignApi::class)

package best.william.kgb

import best.william.kgb.lcd.SDLRenderer
import best.william.kgb.utils.readFile
import kgb.rom.loadCartridge
import kotlinx.cinterop.*
import platform.posix.*


fun main() {
    val fileBytes = readFile("../reference/roms/pokemon-blue.gb")
    if (fileBytes.isEmpty()) {
        println("File is empty or could not be read.")
    } else {
        println("File read successfully, size: ${fileBytes.size} bytes")
    }

    val cartridge = fileBytes.loadCartridge()
    println("Cartridge loaded successfully: $cartridge")
    val bootRom = readFile("../reference/DMG_ROM.bin")

    val renderer = SDLRenderer()
    val gameboy = Gameboy(
        bootRom = bootRom,
        renderer = renderer,
    )

    gameboy.loadCatridge(cartridge)

    while (true) {
        val rendered = gameboy.update()
        // Add a sleep or yield to prevent busy-waiting
        if (rendered) {

        }
    }
}

