package best.william.kgb

import best.william.kgb.lcd.LWJGLRenderer
import best.william.kgb.memory.MemoryMirror
import best.william.kgb.rom.loadCartridge
import kgb.lcd.LCD
import kgb.memory.IORegisters
import kgb.memory.InterruptEnabledMemory
import kgb.memory.MemoryMapper
import kgb.memory.UByteArrayMemory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.io.File
import kotlin.time.ExperimentalTime
import best.william.kgb.cpu.LR35902 as CPU

@ExperimentalTime
@ExperimentalUnsignedTypes
fun main() {
    val bootRom= File("../reference/DMG_ROM.bin").readBytes()
    val cartridge = File("../reference/roms/pokemon-blue.gb").loadCartridge()

    val renderer = LWJGLRenderer()
    val gameboy = Gameboy(
        bootRom,
        renderer,
        renderer
    )

    gameboy.loadCatridge(cartridge)

    println("Running ${cartridge.name}")

    try {

        var previousFrameStart = System.nanoTime()
        while (!renderer.shouldClose()) {
            // Emulate CPU cycles and update LCD
            val renderedFrame = gameboy.update()
            // If a frame was rendered, we wait to maintain a consistent frame rate
            renderer.refresh()
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
