@file:OptIn(ExperimentalUnsignedTypes::class, ExperimentalTime::class)

package best.william.kgb

import best.william.kgb.audio.APU
import best.william.kgb.audio.Speaker
import best.william.kgb.controller.Controller
import best.william.kgb.cpu.LR35902 as CPU
import kgb.lcd.LCD
import kgb.lcd.LCDRenderer
import kgb.memory.IORegisters
import kgb.memory.InterruptEnabledMemory
import kgb.memory.GBMemoryMap
import kgb.memory.UByteArrayMemory
import kgb.rom.Cartridge
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource.Monotonic.markNow
import kotlin.time.toDuration

typealias UpdateCallback = (rendered: Boolean, exitCallback: () -> Unit) -> Unit

class Gameboy(
    bootRom: ByteArray,
    renderer: LCDRenderer = LCD.NullRenderer,
    speaker: Speaker = APU.NullSpeaker,
    controller: Controller = Controller.NullController,
    val enableFrameLimiter: Boolean = true
) {
    val _bootRom = UByteArrayMemory(0x0000u..0x00FFu, bootRom.toUByteArray())

    // I/O Registers 0xFF00 to 0xFF7F
    val ioRegisters = IORegisters()
    val interruptEnabledMemory = InterruptEnabledMemory()

    val cpu = CPU()
    val apu = APU(speaker = speaker)
    val lcd = LCD(renderer)



    val memoryMap = GBMemoryMap(
        boot = _bootRom,
        ioRegisters = ioRegisters,
        interruptEnabledMemory = interruptEnabledMemory,
        oamMemory = lcd.oam
    )

    init {
        cpu.memory = memoryMap
        lcd.memory = memoryMap
        controller.interruptProvider = cpu


        interruptEnabledMemory.attachInterruptRegisters(cpu)
        ioRegisters.attachCPURegisters(cpu)
        ioRegisters.attachController(controller)
        ioRegisters.apu = apu
        ioRegisters.attachLCD(lcd)

        lcd.interruptProvider = cpu
    }

    fun loadCatridge(cartridge: Cartridge) {
        reset()
        memoryMap.cartridge = cartridge
    }

    fun reset() {
        cpu.reset()
        apu.reset()
        lcd.reset()
        memoryMap.reset()
    }

    fun update(): Boolean {
        // Emulate CPU cycles and update LCD
        val cycles = cpu.step()
        apu.update(cycles)
        return lcd.update(cycles)
    }

    fun run(updateCallback: UpdateCallback = { _, _ ->  }) {
        var shouldExit = false
        val exitCallback = {
            shouldExit = true
        }
        val targetFrameTimeNs = (1_000_000_000.0 / 59.7).toDuration(DurationUnit.NANOSECONDS)
        var previousFrameStart = markNow() + targetFrameTimeNs
        while (!shouldExit) {
            val rendered = update()
            updateCallback(rendered, exitCallback)
            // Add a sleep or yield to prevent busy-waiting
            if (rendered) {
                while (enableFrameLimiter && previousFrameStart.hasNotPassedNow()) {
                    // Busy-wait until the target frame time is reached
                }

                previousFrameStart = markNow() + targetFrameTimeNs
            }
        }
    }

    override fun toString(): String {
        return "Gameboy(cpu=$cpu, lcd=$lcd, memoryMap=$memoryMap)"
    }
}