package best.william.kgb

import best.william.kgb.audio.OpenALSpeaker
import best.william.kgb.lcd.LWJGLRenderer
import best.william.kgb.rom.loadCartridge
import java.io.File
import kotlin.time.ExperimentalTime

@ExperimentalTime
@ExperimentalUnsignedTypes
fun main() {
    val bootRom= File("C:\\Users\\wmbes\\IdeaProjects\\kgb\\reference\\DMG_ROM.bin").readBytes()
    val cartridge = File("C:\\Users\\wmbes\\IdeaProjects\\kgb\\reference\\roms\\pokemon-blue.gb").loadCartridge()

    val renderer = LWJGLRenderer()
    val openALSpeaker = OpenALSpeaker()
    val gameboy = Gameboy(
        bootRom = bootRom,
        renderer = renderer,
        speaker = openALSpeaker,
        controller = renderer,
        enableFrameLimiter = false
    )

    gameboy.loadCatridge(cartridge)

    println("Running ${cartridge.name}")

    try {
        gameboy.run { rendered, exit ->
            renderer.refresh()
            if (renderer.shouldClose()) exit() // Run waits for the calling application to tell it to stop
        }
    } catch (e: Exception) {
        println(e.printStackTrace())
        println(gameboy)
    } finally {
        renderer.dispose()
    }
}
