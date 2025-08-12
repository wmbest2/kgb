@file:OptIn(ExperimentalForeignApi::class, ExperimentalTime::class)

package best.william.kgb

import best.william.kgb.utils.readFile
import kgb.lcd.LCDRenderer
import kgb.rom.loadCartridge
import kotlinx.cinterop.ExperimentalForeignApi
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource.Monotonic.markNow


fun main() {
    val startTime = markNow()
    val fileBytes = readFile("/mnt/c/Users/wmbes/IdeaProjects/kgb/reference/roms/pokemon-blue.gb")
    if (fileBytes.isEmpty()) {
        println("File is empty or could not be read.")
    } else {
        println("File read successfully, size: ${fileBytes.size} bytes")
    }

    val cartridge = fileBytes.loadCartridge()
    println("Cartridge loaded successfully: $cartridge")
    val bootRom = readFile("/mnt/c/Users/wmbes/IdeaProjects/kgb/reference/DMG_ROM.bin")

    val frameRateCountingRenderer = FrameCountingRenderer()

    val gameboy = Gameboy(
        bootRom = bootRom,
        renderer = frameRateCountingRenderer,
        enableFrameLimiter = false
    )

    gameboy.loadCatridge(cartridge)

    println("Took ${startTime.elapsedNow()} to load cartridge and boot ROM")
    var frameCount = 0

    gameboy.run()

    println("Frame count: $frameCount, took ${startTime.elapsedNow()} to run the gameboy for 597 frames")
}

class FrameCountingRenderer : LCDRenderer {
    private val startTime = markNow()
    private var frameCount = 0

    override fun render(pixels: UByteArray) {
        frameCount++
        if (frameCount % 1000 == 0) {
            val elapsed = startTime.elapsedNow()
            val fps: Double = frameCount.toDouble() / elapsed.toDouble(DurationUnit.SECONDS)
            println("FPS: $fps Frames rendered: $frameCount, Time elapsed: ${elapsed.inWholeMilliseconds} ms")
        }
    }
}