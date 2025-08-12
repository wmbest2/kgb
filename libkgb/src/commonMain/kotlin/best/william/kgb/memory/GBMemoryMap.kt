package kgb.memory

import best.william.kgb.memory.MemoryMirror

import kgb.rom.Cartridge

@ExperimentalUnsignedTypes
class GBMemoryMap(
    private val boot: IMemory,
    val vram: UByteArrayMemory = UByteArrayMemory(0x8000u..0x9FFFu),
    val wram: UByteArrayMemory = UByteArrayMemory(0xC000u..0xDFFFu),
    val echoRam: IMemory = MemoryMirror(0xE000u..0xFDFFu, wram),
    val hram: UByteArrayMemory = UByteArrayMemory(0xFF80u..0xFFFEu),
    val ioRegisters: IORegisters = IORegisters(),
    val interruptEnabledMemory: InterruptEnabledMemory = InterruptEnabledMemory(),
    val oamMemory: IMemory
): IMemory {

    private var bootEnabled = true

    var cartridge: Cartridge? = null

    override var addressRange: UIntRange = 0x0000u..0xFFFFu
        private set

    fun reset() {
        vram.clear()
        wram.clear()
        hram.clear()
        bootEnabled = true
        cartridge = null
    }

    override fun set(position: UShort, value: UByte) {
        if (bootEnabled && position < 0x0100u.toUShort()) {
            boot.set(position, value)
            return
        }

        when (position) {
            in 0x0000u..0x7FFFu -> {
                // ROM area, typically handled by the cartridge
                cartridge?.set(position, value) ?: return
            }
            in 0x8000u..0x9FFFu -> {
                // VRAM area, typically handled by the cartridge or video memory
                vram.set(position, value)
            }
            in 0xC000u..0xDFFFu -> {
                // WRAM area, typically handled by the cartridge or working RAM
                wram.set(position, value)
            }
            in 0xA000u..0xBFFFu -> {
                // RAM area, typically handled by the cartridge
                cartridge?.set(position, value) ?: return
            }
            in 0xE000u..0xFDFFu -> {
                // Echo RAM area, typically a mirror of WRAM
                echoRam.set(position, value)
            }
            in 0xFE00u..0xFE9Fu -> {
                // OAM area, typically handled by the OAM memory chunk
                oamMemory.set(position, value)
            }
            in 0xFF00u..0xFF7Fu -> {
                ioRegisters.set(position, value)
                if (position == 0xFF50u.toUShort()) {
                    // Disable boot ROM after the write to 0xFF50 has been processed
                    bootEnabled = false
                }
            }
            in 0xFF80u..0xFFFEu -> {
                // HRAM area, handled by the hram memory chunk
                hram.set(position, value)
                return
            }
            0xFFFFu.toUShort() -> {
                // Interrupt enable register, typically handled by the interrupt enabled memory
                interruptEnabledMemory.set(position, value)
            }
            else -> {
                // Default case for unhandled addresses
                return
            }
        }
    }


    override fun get(position: UShort): UByte {
        if (bootEnabled && position < 0x0100u.toUShort()) return boot[position] // Boot ROM disabled
        return when(position) {
            in 0x0000u..0x7FFFu -> {
                // ROM area, typically handled by the cartridge
                cartridge?.get(position) ?: 0u
            }
            in 0x8000u..0x9FFFu -> {
                // VRAM area, typically handled by the cartridge or video memory
                vram[position]
            }
            in 0xC000u..0xDFFFu -> {
                // WRAM area, typically handled by the cartridge or working RAM
                wram[position]
            }
            in 0xE000u..0xFDFFu -> {
                // Echo RAM area, typically a mirror of WRAM
                echoRam[position]
            }
            in 0xA000u..0xBFFFu -> {
                // RAM area, typically handled by the cartridge
                cartridge?.get(position) ?: 0u
            }
            in 0xFE00u..0xFE9Fu -> {
                // OAM area, typically handled by the OAM memory chunk
                oamMemory.get(position)
            }
            in 0xFF00u..0xFF7Fu -> {
                // I/O registers, not implemented here
                ioRegisters.get(position)
            }
            in 0xFF80u..0xFFFEu -> {
                // HRAM area, handled by the hram memory chunk
                hram[position]
            }
            0xFFFFu.toUShort() -> {
                // Interrupt enable register, typically handled by the interrupt enabled memory
                interruptEnabledMemory.get(position)
            }
            else -> {
                // Default case for unhandled addresses
                0u
            }
        }
    }
}