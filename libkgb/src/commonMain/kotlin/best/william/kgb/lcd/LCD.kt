package kgb.lcd

import best.william.kgb.cpu.flag
import best.william.kgb.cpu.masked
import co.touchlab.kermit.Logger
import co.touchlab.kermit.NoTagFormatter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter
import kgb.cpu.InterruptProvider
import kgb.memory.IMemory
import kgb.memory.UByteArrayMemory
import kgb.util.bit
import kotlin.collections.chunked

typealias Renderer = (ByteArray) -> Unit

interface LCDRenderer {
    fun render(pixels: UByteArray)
}

@ExperimentalUnsignedTypes
class LCD(
    private val memory: IMemory,
    private val renderer: LCDRenderer,
) {
    private val logger = Logger(
        config = loggerConfigInit(
            platformLogWriter(NoTagFormatter),
            minSeverity = Severity.Debug,
        ),
        tag = "LCD"
    )

    var interruptProvider: InterruptProvider? = null
    // region Status Flags

    var STAT: UByte = 0x85u
    val LYCInterruptEnable: Boolean by flag(::STAT, 6)
    val OAMInterruptEnable: Boolean by flag(::STAT, 5)
    val VBlankInterruptEnable: Boolean by flag(::STAT, 4)
    val HBlankInterruptEnable: Boolean by flag(::STAT, 3)
    val LYCoincident: Boolean by flag(::STAT, 2)
    // Mode is the bottom 2 bits of STAT
    val mode: UByte by masked(::STAT, 3u)

    // endregion

    // region LCD Control

    var LCDC: UByte = 91u
    val LCDEnabled by flag(::LCDC, 7)
    val WindowTileMapSelect by flag(::LCDC, 6)
    val WindowEnabled by flag(::LCDC, 5)
    val BGWindowTileDataSelect by flag(::LCDC, 4)
    val BGTileMapSelect by flag(::LCDC, 3)
    val OBJSize by flag(::LCDC, 2)
    val OBJEnabled by flag(::LCDC, 1)
    val BGWindowEnabled by flag(::LCDC, 0)
    // endregion


    // region IO Registers
    var scrollX: UByte = 0u
    var scrollY: UByte = 0u
    var LY: UByte = 0u
    var LYC: UByte = 0u
    var DMA: UByte
        get() = memory[0xFF46u]
        set(value) {
            logger.d { "DMA transfer initiated with value: $value" }
            memory[0xFF46u] = value
            val startAddress = (value.toInt() shl 8) and 0xFF00
            for (i in 0 until 160) {
                memory.set((0xFE00u + i.toUInt()).toUShort(), memory[(startAddress + i).toUShort()])
            }
        }
    var BGP: UByte = 0u
    var OBP0: UByte = 0u
    var OBP1: UByte = 0u
    var WY: UByte = 0u
    var WX: UByte = 0u

    // endregion

    private var scanlineCounter = 456
    val pixelBuffer: UByteArray = UByteArray(160 * 144)

    private fun drawScanline() {
        val line = LY
        if (line !in 0u until 144u) return
        val pixels = UByteArray(160)
        // --- Background/Window ---
        if (BGWindowEnabled) {
            for (x in 0u until 160u) {
                val useWindow = WindowEnabled && x + 7u >= WX && WY <= line

                val winTileMapBase = if (WindowTileMapSelect) 0x9C00u else 0x9800u
                val bgTileMapBase = if (BGTileMapSelect) 0x9C00u else 0x9800u
                val tileDataBase = if (BGWindowTileDataSelect) 0x8000u else 0x8800u

                val (tileMapBase, scrolledX, scrolledY) = if (useWindow) {
                    val winX = x - (WX - 7u)
                    val winY = line - WY
                    Triple(winTileMapBase, winX, winY)
                } else {
                    Triple(bgTileMapBase, (x + scrollX) and 0xFFu, (line + scrollY) and 0xFFu)
                }

                val tileMapX = scrolledX / 8u
                val tileMapY = scrolledY / 8u
                val tileMapOffset = tileMapBase + (tileMapY * 32u + tileMapX)
                val tileIndex = memory[tileMapOffset.toUShort()]
                val tileNum = if (tileDataBase == 0x8000u) tileIndex else (tileIndex.toInt() xor 0x80).toUByte()
                val tileAddr = tileDataBase + (tileNum * 16u)
                val pixelY = scrolledY % 8u
                val byte1 = memory[(tileAddr + (pixelY * 2u)).toUShort()]
                val byte2 = memory[(tileAddr + (pixelY * 2u + 1u)).toUShort()]
                val pixelX = 7u - (scrolledX % 8u)
                val colorNum = (((byte2.toUInt() shr pixelX.toInt()) and 1u) shl 1) or ((byte1.toUInt() shr pixelX.toInt()) and 1u)
                val shade = ((BGP.toInt() shr (colorNum.toInt() * 2)) and 0b11).toUByte()
                if (x == 0u) {
                    logger.v { "line=$line BGP=$BGP tileIndex=$tileIndex tileNum=$tileNum byte1=$byte1 byte2=$byte2" }
                }
                pixels[x.toInt()] = shade
            }
        }
        // --- Sprites ---
        if (OBJEnabled) {
            val spriteHeight = if (OBJSize) 16u else 8u
            val spritesOnLine = mutableListOf<Pair<UInt, UInt>>() // Pair: OAM index, X
            for (i in 0u until 40u) {
                val base = (i * 4u).toUShort()
                val spriteY = memory[base] - 16u
                val spriteX = memory[(base + 1u).toUShort()] - 8u
                if (line in spriteY until (spriteY + spriteHeight)) {
                    spritesOnLine.add(Pair(i, spriteX))
                    if (spritesOnLine.size >= 10) break // Max 10 sprites per line
                }
            }
            for ((i, spriteX) in spritesOnLine) {
                val base = (i * 4u).toUShort()
                val spriteY = memory[base] - 16u
                val tileIndex = memory[(base + 2u).toUShort()]
                val flags = memory[(base + 3u).toUShort()]
                val yFlip = flags.bit(6)
                val xFlip = flags.bit(5)
                val palette = if (flags.bit(4)) OBP1 else OBP0
                val priority = flags.bit(7)
                val pixelY = if (!yFlip) line - spriteY else spriteHeight - 1u - (line - spriteY)
                val tileAddr = 0x8000u + (tileIndex * 16u + pixelY * 2u)
                val byte1 = memory[(tileAddr).toUShort()]
                val byte2 = memory[(tileAddr + 1u).toUShort()]
                for (px in 0u until 8u) {
                    val x = if (!xFlip) px else 7u - px
                    val screenX = spriteX + px
                    if (screenX !in 0u until 160u) continue
                    val colorNum = (((byte2.toInt() shr (7 - x.toInt())) and 1) shl 1) or ((byte1.toInt() shr (7 - x.toInt())) and 1)
                    if (colorNum == 0) continue // Transparent
                    val shade = ((palette.toInt() shr (colorNum * 2)) and 0b11).toUByte()
                    if (!priority || pixels[screenX.toInt()].toUInt() == 0u) {
                        pixels[screenX.toInt()] = shade
                    }
                }
            }

        }

        for (x in 0 until 160) {
            pixelBuffer[line.toInt() * 160 + x] = pixels[x]
        }
        // Debug: Print nonzero pixel count for this scanline
        val nonZeroCount = pixels.count { it != 0u.toUByte() }
        logger.v { "Scanline $line: Nonzero pixels = $nonZeroCount" }
    }

    fun renderScreen() {
        logger.i { "VBLANK: Rendering screen" }
        renderer.render(pixelBuffer)
        // Render all pixels as ASCII for debugging
        val asciiShades = arrayOf(' ', '░', '▒', '▓')
        pixelBuffer.chunked(160).map {
            it.joinToString(separator = "") { shade ->
                asciiShades[shade.toInt()].toString()
            }
        }.forEach {
            logger.d("SCREEN: $it")
        }
    }

    /**
     * Checks STAT conditions and requests STAT interrupt if needed.
     * Should be called whenever LY, LYC, or mode changes.
     */
    fun checkAndRequestSTATInterrupt() {
        // LY=LYC coincidence
        val lyCoincidenceNow = LY == LYC
        if (lyCoincidenceNow != LYCoincident) {
            // Update LYCoincident flag
            STAT = if (lyCoincidenceNow) STAT or 0x04u else STAT and 0xFBu
            // Request STAT interrupt if enabled
            if (LYCInterruptEnable && lyCoincidenceNow) {
                interruptProvider?.requestInterrupt(1) // STAT interrupt is ID 1
            }
        }
        // Mode change interrupts
        when (mode.toInt()) {
            0 -> if (HBlankInterruptEnable) interruptProvider?.requestInterrupt(1) // HBlank
            1 -> if (VBlankInterruptEnable) interruptProvider?.requestInterrupt(1) // VBlank
            2 -> if (OAMInterruptEnable) interruptProvider?.requestInterrupt(1) // OAM
            // Mode 3 (Transfer) does not trigger STAT interrupt
        }
    }

    fun update(cycles: Int) {
        if (!LCDEnabled) return
        scanlineCounter -= cycles

        if (scanlineCounter <= 0) {
            val currentLine = (++LY).toUInt()
            scanlineCounter += 456

            checkAndRequestSTATInterrupt()

            when {
                currentLine < 144u -> {
                    logger.v { "Drawing scanline $currentLine" }
                    drawScanline()
                }
                currentLine == 144u -> {
                    logger.i { "VBLANK interrupt requested at scanline $currentLine" }
                    drawScanline()
                    interruptProvider?.requestInterrupt(0) // VBLANK
                    renderScreen() // Render the whole screen at VBLANK
                }
                currentLine > 153u -> {
                    logger.v { "Resetting LY to 0 after scanline $currentLine" }
                    LY = 0u
                    drawScanline()
                }
            }
        }
    }
}
