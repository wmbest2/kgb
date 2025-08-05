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
    val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val bootRomBytes = File("../reference/DMG_ROM.bin").readBytes().toUByteArray()
    val bootRom = UByteArrayMemory(0x0000u..0x00FFu, bootRomBytes)
    // Rom range: 0x0100 to 0x7FFF
    // Optional Rom Bank 0x8000 to 0x9FFF
    //val cartridge = File("../reference/roms/test/cpu_instrs/individual/02-interrupts.gb").loadCartridge()
    //val cartridge = File("../reference/roms/test/oam_bug/oam_bug.gb").loadCartridge()
//    val cartridge = File("../reference/roms/test/interrupt_time/interrupt_time.gb").loadCartridge()
//    val cartridge = File("../reference/roms/test/cpu_instrs.gb").loadCartridge()
    val cartridge = File("../reference/roms/pokemon-blue.gb").loadCartridge()
//    val cartridge = File("../reference/roms/super-mario-land.gb").loadCartridge()

    val vram = UByteArrayMemory(0x8000u..0x9FFFu)
    val wram = UByteArrayMemory(0xC000u..0xDFFFu)
    val echoRam = MemoryMirror(0xE000u..0xFDFFu, wram)

    // I/O Registers 0xFF00 to 0xFF7F
    val ioRegisters = IORegisters()

    // Unused memory 0xFF80 to 0xFFFE
    // This is typically used for high RAM (HRAM) and is not directly accessible by the CPU,
    // but can be used for temporary storage or flags.
    val hram = UByteArrayMemory(0xFF80u..0xFFFEu)
    val interruptEnabledMemory = InterruptEnabledMemory()

    val cpu = CPU()
    val renderer = LWJGLRenderer(cpu)
    val lcd = LCD(renderer)

    val memoryMap = MemoryMapper(
        bootRom, // Boot ROM is usually only used for the first few cycles
        vram, // Video RAM 0x8000 to 0x9FFF
        wram,
        echoRam,
        lcd.oam,
        ioRegisters,
        hram,
        *cartridge.getMemoryChunks(),
        interruptEnabledMemory
    )

    cpu.memory = memoryMap
    lcd.memory = memoryMap

    cartridge.getMemoryChunks().forEach { println("${it.addressRange.start.toString(16)}:${it.addressRange.endInclusive.toString(16)}") }



    interruptEnabledMemory.attachInterruptRegisters(cpu)
    ioRegisters.attachCPURegisters(cpu)
    ioRegisters.attachController(renderer)
    ioRegisters.attachLCD(lcd)

    lcd.interruptProvider = cpu

    println("Running ${cartridge.name}")

    try {

        var previousFrameStart = System.nanoTime()
        while (!renderer.shouldClose()) {

            // Emulate CPU cycles and update LCD
            val cycles = cpu.step()
            val renderedFrame = lcd.update(cycles)

            // If a frame was rendered, we wait to maintain a consistent frame rate
            if (renderedFrame) {
                var elapsed = System.nanoTime() - previousFrameStart
                val targetFrameTimeNs = (1_000_000_000.0 / 59.7).toLong()
                while (elapsed < targetFrameTimeNs) {
                    Thread.yield()
                    elapsed = System.nanoTime() - previousFrameStart
                }
                previousFrameStart = System.nanoTime()
            }
        }
    } catch (e: Exception) {
        println(e.printStackTrace())
        println(cpu)
    } finally {
        renderer.dispose()
    }
}
