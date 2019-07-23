package kgb.lcd

import best.william.kgb.cpu.flag
import best.william.kgb.cpu.masked
import kgb.cpu.InterruptProvider
import kgb.util.masks

typealias Renderer = (ByteArray) -> Unit

@ExperimentalUnsignedTypes
class LCD(
        val renderer: Renderer = {}
) {

    var interruptProvider: InterruptProvider? = null
    // region Status Flags

    var STAT: UByte = 0u
    val LYCInterrupt: Boolean by flag(::STAT, 6)
    val OAMInterrupt: Boolean by flag(::STAT, 5)
    val VBlankInterrupt: Boolean by flag(::STAT, 4)
    val HBlankInterrupt: Boolean by flag(::STAT, 3)
    val LYCoincident: Boolean by flag(::STAT, 2)
    // Mode is the bottom 2 bits of STAT
    val mode: UByte by masked(::STAT, 3u)

    // endregion

    // region LCD Control

    var LCDC: UByte = 0u
    val LCDEnabled by flag(::LCDC, 7)
    val WindowTileMapSelect by flag(::LCDC, 6) // False = 0, True = 1

    // endregion


    // region IO Registers
    var scrollX: UByte = 0u
    var scrollY: UByte = 0u
    var LY: UByte = 0u
    var LYC: UByte = 0u
    var DMA: UByte = 0u
    var BGP: UByte = 0u
    var OBP0: UByte = 0u
    var OBP1: UByte = 0u
    var WY: UByte = 0u
    var WX: UByte = 0u

    // endregion

    private var scanlineCounter = 0

    fun update(cycles: UByte) {
        if (!LCDEnabled) return
        scanlineCounter -= cycles.toInt()

        if (scanlineCounter <= 0) {
            val currentLine = (++LY).toUInt()
            scanlineCounter += 456

            when {
                currentLine < 144u -> {
                    drawScanline()
                }
                currentLine == 144u -> {
                    drawScanline()
                    interruptProvider?.requestInterrupt(0) // VBLANK
                }
                currentLine > 153u -> {
                    LY = 0u
                    drawScanline()
                }
                else -> {}
            }
        }
    }

    private fun drawScanline() {

    }
}
