package best.william.kgb

import best.william.kgb.audio.OpenALSpeaker
import best.william.kgb.lcd.LWJGLRenderer
import best.william.kgb.rom.loadCartridge
import java.io.File
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalUnsignedTypes
fun main() {
    val bootRom= File("../reference/DMG_ROM.bin").readBytes()
    val cartridge = File("../reference/roms/pokemon-blue.gb").loadCartridge()

    val renderer = LWJGLRenderer()
    val openALSpeaker = OpenALSpeaker()
    val gameboy = Gameboy(
        bootRom = bootRom,
        renderer = renderer,
        speaker = openALSpeaker,
        controller = renderer
    )

    gameboy.loadCatridge(cartridge)

    println("Running ${cartridge.name}")

    try {

        var previousFrameStart = System.nanoTime()
        while (!renderer.shouldClose()) {
            // Emulate CPU cycles and update LCD
            val renderedFrame = gameboy.update()
            renderer.refresh()
            // If a frame was rendered, we wait to maintain a consistent frame rate
            if (renderedFrame) {
                var elapsed = System.nanoTime() - previousFrameStart
                val targetFrameTimeNs = (1_000_000_000.0 / 59.7).toLong()
                while (elapsed < targetFrameTimeNs) {
                    elapsed = System.nanoTime() - previousFrameStart
                }
                previousFrameStart = System.nanoTime()
            }
        }
    } catch (e: Exception) {
        println(e.printStackTrace())
        println(gameboy)
    } finally {
        renderer.dispose()
    }
}
