package kgb.lcd

import best.william.kgb.cpu.flag
import co.touchlab.kermit.Logger
import co.touchlab.kermit.NoTagFormatter
import co.touchlab.kermit.Severity
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter
import kgb.cpu.InterruptProvider
import kgb.memory.IMemory
import kgb.util.bit
import kotlin.collections.chunked
import kotlin.jvm.JvmInline

typealias Renderer = (ByteArray) -> Unit

interface LCDRenderer {
    fun render(pixels: UByteArray)
}

@ExperimentalUnsignedTypes
class LCD(
    private val renderer: LCDRenderer,
    private val enableOAMBug: Boolean = true,
) {
    private val logger = Logger(
        config = loggerConfigInit(
            platformLogWriter(NoTagFormatter),
            minSeverity = Severity.Error,
        ),
        tag = "LCD"
    )

    private val lineBuffer = UByteArray(160)

    lateinit var memory: IMemory

    var interruptProvider: InterruptProvider? = null
    // region Status Flags

    var STAT: UByte = 0x85u
    val LYCInterruptEnable: Boolean by flag(::STAT, 6)
    val OAMInterruptEnable: Boolean by flag(::STAT, 5)
    val VBlankInterruptEnable: Boolean by flag(::STAT, 4)
    val HBlankInterruptEnable: Boolean by flag(::STAT, 3)
    val LYCoincident: Boolean by flag(::STAT, 2)
    // Mode is the bottom 2 bits of STAT
    val mode: UByte
        get() {
            return STAT and 0x03u // Mask to get the bottom 2 bits
        }

    // endregion

    // region LCD Control

    var LCDC: UByte = 91u // 0b01011011
        set(value) {
            logger.d { "LCDC set to ${value.toString(2).padStart(8, '0')}" }
            field = value
            // When LCDC is disabled, reset LY and mode
            if (!value.bit(7)) {
                LY = 0u
                scanlineCounter = 456
                STAT = STAT and 0xFCu // Reset mode bits
                logger.i { "LCDC disabled, LY reset to 0 and mode cleared" }
            }
        }

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
    var _dma: UByte = 0u // Backing field for DMA register, not used directly
    var DMA: UByte
        get() = _dma
        set(value) {
            logger.d { "DMA transfer initiated with value: $value" }
            val startAddress = (value.toInt() shl 8) and 0xFF00
            for (i in 0 until 160) {
                memory[(0xFE00u + i.toUInt()).toUShort()] = memory[(startAddress + i).toUShort()]
            }
        }
    var BGP: UByte = 0u
    var OBP0: UByte = 0u
    var OBP1: UByte = 0u
    var WY: UByte = 0u
    var WX: UByte = 0u

    val oam: IMemory
        get() = object : IMemory {
            val data = UByteArray(160) // OAM data is 160 bytes (40 sprites * 4 bytes each)
            private val MODE_2 = 2u.toUByte()
            private val MODE_3 = 3u.toUByte()

            private inline fun checkOAMBug(): Boolean {
                return enableOAMBug && (mode == MODE_2 || mode == MODE_3)
            }

            override fun get(position: UShort): UByte {
                if (checkOAMBug()) {
                    // OAM Bug: Read during mode 2 or 3 returns 0xFF
                    logger.w { "OAM read ignored in mode $mode at position $position" }
                    return 0xFFu
                } else {
                    // Normal OAM read
                    logger.v { "OAM read at $position" }
                    return data[(position - 0xFE00u).toInt()]
                }
            }

            override fun set(position: UShort, value: UByte) {
                if (checkOAMBug()) {
                    // OAM Bug: Write to OAM during mode 2 or 3 is ignored
                    logger.w { "OAM write ignored in mode $mode at position $position" }
                } else {
                    // Normal OAM write
                    logger.v { "OAM write at $position with value $value" }
                    data[(position - 0xFE00u).toInt()] = value
                }
            }

            override val addressRange: UIntRange = 0xFE00u..0xFE9Fu // OAM range
        }
    // endregion

    fun reset() {
        logger.i { "Resetting LCD state" }
        LCDC = 0x91u // Default LCDC value
        STAT = 0x85u // Default STAT value
        scrollX = 0u
        scrollY = 0u
        LY = 0u
        LYC = 0u
        _dma = 0u
        BGP = 0u
        OBP0 = 0u
        OBP1 = 0u
        WY = 0u
        WX = 0u
        scanlineCounter = 456 // Start with the first scanline ready to draw
        frameCycleCounter = 0 // Reset frame cycle counter
    }


    private var scanlineCounter = 0
    private var frameCycleCounter = 0 // Track cycles for the current frame
    val pixelBuffer: UByteArray = UByteArray(160 * 144)


    val spritesOnLine = ArrayList<Sprite>(10) // Pair: OAM index, X

    private fun drawScanline() {
        val line = LY
        if (line !in 0u until 144u) return
        val pixels = lineBuffer
        // --- Background/Window ---
        if (BGWindowEnabled) {
            renderBackgrounds(line, pixels)
        }
        // --- Sprites ---
        if (OBJEnabled) {
            renderSprites(line, pixels)
        }

        for (x in 0 until 160) {
            pixelBuffer[line.toInt() * 160 + x] = pixels[x]
        }
        // Debug: Print nonzero pixel count for this scanline
        val nonZeroCount = pixels.count { it != 0u.toUByte() }
        logger.v { "Scanline $line: Nonzero pixels = $nonZeroCount" }
    }

    private fun renderBackgrounds(line: UByte, pixels: UByteArray) {
        val winTileMapBase = if (WindowTileMapSelect) 0x9C00u else 0x9800u
        val bgTileMapBase = if (BGTileMapSelect) 0x9C00u else 0x9800u
        val tileDataBase = if (BGWindowTileDataSelect) 0x8000u else 0x8800u
        val windowEnabled = WindowEnabled
        val wx = WX.toUInt()
        val wy = WY.toUInt()

        for (x in 0u until 160u) {
            val useWindow = windowEnabled && x + 7u >= wx && wy <= line

            var tileMapBase: UInt
            var scrolledX: UInt
            var scrolledY: UInt

            if (useWindow) {
                tileMapBase = winTileMapBase
                scrolledX = x - (wx - 7u)
                scrolledY = line - wy
            } else {
                tileMapBase = bgTileMapBase
                scrolledX = (x + scrollX) and 0xFFu
                scrolledY = (line + scrollY) and 0xFFu
            }

            val tileMapX = scrolledX / 8u
            val tileMapY = scrolledY / 8u
            val tileMapOffset = tileMapBase + (tileMapY * 32u + tileMapX)
            val tileIndex = memory[tileMapOffset.toUShort()]
/*            logger.v {
                "BG/Window: line=$line x=$x useWindow=$useWindow tileMapBase=${tileMapBase.toString(16)} scrolledX=$scrolledX scrolledY=$scrolledY tileMapOffset=${
                    tileMapOffset.toString(
                        16
                    )
                } tileIndex=$tileIndex"
            }*/
            val tileNum = if (tileDataBase == 0x8000u) tileIndex else (tileIndex.toShort() + 128).toUShort().toUByte()
            val tileAddr = tileDataBase + (tileNum * 16u)
            val pixelY = scrolledY % 8u
            val byte1 = memory[(tileAddr + (pixelY * 2u)).toUShort()]
            val byte2 = memory[(tileAddr + (pixelY * 2u + 1u)).toUShort()]
            val pixelX = scrolledX % 8u
            val bit = 7 - pixelX.toByte()
            val colorNum = (((byte2.toInt() shr bit) and 1) shl 1) or ((byte1.toInt() shr bit) and 1)
            val shade = ((BGP.toInt() shr (colorNum * 2)) and 0b11).toUByte()
/*            if (line.toInt() == 16) {
                logger.v {
                    "BG: line=$line x=$x tileMapOffset=${tileMapOffset.toString(16)} tileIndex=$tileIndex tileNum=$tileNum tileAddr=${
                        tileAddr.toString(
                            16
                        )
                    } pixelY=$pixelY pixelX=$pixelX colorNum=$colorNum shade=$shade"
                }
            }*/

            pixels[x.toInt()] = shade
        }
    }

    @JvmInline
    value class Sprite(val value: Int) {
        constructor(
            x: UInt,
            y: UInt,
            tileIndex: UByte,
            flags: UByte
        ) : this(
            (x.toInt() shl 24) or (y.toInt() shl 16) or (tileIndex.toInt() shl 8) or flags.toInt()
        )

        val x: UInt get() = ((value shr 24) and 0xFF).toUInt()
        val y: UInt get() = ((value shr 16) and 0xFF).toUInt()
        val tileIndex: UByte get() = ((value shr 8) and 0xFF).toUByte()
        val flags: UByte get() = (value and 0xFF).toUByte()
    }

    private fun renderSprites(line: UByte, pixels: UByteArray) {
        logger.i { "Drawing sprites for line $line" }
        val largeSprites = OBJSize // true if 8x16 sprites, false if 8x8
        val spriteHeight = if (largeSprites) 16u else 8u
        spritesOnLine.clear()
        for (i in 0u until 40u) {
            val base = ((i * 4u) + 0xFE00u).toUShort()
            val spriteY = (memory[base] - 16u)
            val spriteX =(memory[(base + 1u).toUShort()] - 8u)
            val spriteTileIndex = memory[(base + 2u).toUShort()] // Tile index is at offset 2
            val flags = memory[(base + 3u).toUShort()] // Flags are

            val sprite = Sprite(
                x = spriteX,
                y = spriteY,
                tileIndex = spriteTileIndex,
                flags = flags
            )

            if (line in sprite.y until (sprite.y + spriteHeight)) {
                spritesOnLine.add(sprite)
                if (spritesOnLine.size >= 10) break // Max 10 sprites per line
            }
        }

        // Sort sprites by X position for proper priority (leftmost sprite has highest priority)
        spritesOnLine.sortBy { it.x }

        for (sprite in spritesOnLine) {
            val spriteX = sprite.x
            val spriteY = sprite.y
            var tileIndex = sprite.tileIndex
            val flags = sprite.flags

            // For 8x16 sprites, use consecutive tiles and ignore bit 0
            if (largeSprites) {
                tileIndex = tileIndex and 0xFEu
            }

            val yFlip = flags.bit(6)
            val xFlip = flags.bit(5)
            val palette = if (flags.bit(4)) OBP1 else OBP0
            val priority = flags.bit(7) // true = behind background (except color 0)

            val pixelY = if (!yFlip) line - spriteY else spriteHeight - 1u - (line - spriteY)

            // For 8x16 sprites, determine which tile to use (top or bottom)
            val actualTileIndex = if (largeSprites && pixelY >= 8u) {
                (tileIndex + 1u).toUByte()
            } else {
                tileIndex
            }
            val actualPixelY = if (largeSprites && pixelY >= 8u) pixelY - 8u else pixelY

            val tileAddr = 0x8000u + (actualTileIndex * 16u + actualPixelY * 2u)
            val byte1 = memory[(tileAddr).toUShort()]
            val byte2 = memory[(tileAddr + 1u).toUShort()]
            val ZERO = 0u.toUByte() // Transparent color

            for (px in 0u until 8u) {
                val x = if (!xFlip) px else 7u - px
                val screenX = spriteX + px
                if (screenX !in 0u until 160u) continue

                val colorNum =
                    (((byte2.toInt() shr (7 - x.toInt())) and 1) shl 1) or ((byte1.toInt() shr (7 - x.toInt())) and 1)
                if (colorNum == 0) continue // Transparent

                val shade = ((palette.toInt() shr (colorNum * 2)) and 0b11).toUByte()

                // Fixed priority logic:
                // - If priority = false (0), sprite is always in front
                // - If priority = true (1), sprite is behind background unless background is color 0
                val bgIsTransparent = pixels[screenX.toInt()] == ZERO
                if (!priority || bgIsTransparent) {
                    pixels[screenX.toInt()] = shade
                }

                logger.v { "Sprite: spriteX=$spriteX spriteY=$spriteY tileIndex=$actualTileIndex pixelY=$actualPixelY xFlip=$xFlip yFlip=$yFlip colorNum=$colorNum shade=$shade priority=$priority" }
            }
        }
    }

    fun renderScreen() {
        logger.i { "VBLANK: Rendering screen" }
        logger.i {
            """
            ┌─────────────────────────────┐
            │ Rendering screen: 160x144   │
            ├─────────────────────────────┤
            │ Pixel buffer size: ${pixelBuffer.size} bytes
            │
            │ ── OTHER Flags ─────────────
            │ LCDC:                   $LCDC
            │ BGP:                    $BGP
            │ OBP0:                   $OBP0
            │ OBP1:                   $OBP1
            │ Scroll X:              $scrollX
            │ Scroll Y:              $scrollY
            │ LYC:                   $LYC
            │ WY:                    $WY
            │ WX:                    $WX
            │ ── LCDC Flags ──────────────
            │ LCD Enabled:           $LCDEnabled
            │ Window Tile Map Select:$WindowTileMapSelect
            │ Window Enabled:        $WindowEnabled
            │ BG/Window Tile Data:   $BGWindowTileDataSelect
            │ BG Tile Map Select:    $BGTileMapSelect
            │ OBJ Size:              $OBJSize
            │ OBJ Enabled:           $OBJEnabled
            │ BG/Window Enabled:     $BGWindowEnabled
            │
            │ ── STAT Flags ──────────────
            │ LYC Interrupt Enable:  $LYCInterruptEnable
            │ OAM Interrupt Enable:  $OAMInterruptEnable
            │ VBlank Interrupt Enable:$VBlankInterruptEnable
            │ HBlank Interrupt Enable:$HBlankInterruptEnable
            │ LY Coincident:         $LYCoincident
            │ Mode:                  $mode
            └─────────────────────────────┘
            """.trimIndent()
        }
        renderer.render(pixelBuffer)
        // Render all pixels as ASCII for debugging
        if (logger.config.minSeverity <= Severity.Verbose) {
            val asciiShades = arrayOf(' ', '░', '▒', '▓')
            pixelBuffer.chunked(160).map {
                it.joinToString(separator = "") { shade ->
                    asciiShades[shade.toInt()].toString()
                }
            }.forEach {
                logger.v("SCREEN: $it")
            }
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
            // Update LYCoincidence flag
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

    fun update(cycles: Int): Boolean {
        if (!LCDEnabled) return false
        scanlineCounter -= cycles
        frameCycleCounter += cycles

        var rendered = false
        if (scanlineCounter <= 0) {
            scanlineCounter += 456
            val currentLine = LY

            checkAndRequestSTATInterrupt()

            var reset = false
            when {
                currentLine < 144u -> {
                    logger.v { "Drawing scanline $LY" }
                    drawScanline()
                }

                currentLine == 144u.toUByte() -> {
                    logger.i { "VBLANK interrupt requested at scanline $LY" }
                    interruptProvider?.requestInterrupt(0) // VBLANK
                    renderScreen() // Render the whole screen at VBLANK
                    rendered =  true
                }

                currentLine > 153u -> {
                    logger.v { "Resetting LY to 0 after scanline $LY" }
                    reset = true
                }
            }

            // More accurate LY increment logic
            if (reset) {
                LY = 0u
            } else {
                LY++
            }
        }
        return rendered
    }

    object NullRenderer : LCDRenderer {
        override fun render(pixels: UByteArray) {
            // No-op renderer, does nothing
        }
    }
}
