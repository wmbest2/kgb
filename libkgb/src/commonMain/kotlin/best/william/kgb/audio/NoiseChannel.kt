package best.william.kgb.audio

import best.william.kgb.cpu.masked
import best.william.kgb.util.scale4BitTo
import kgb.util.ZERO
import kgb.util.bit
import kotlin.math.pow

class NoiseChannel(
    var NR41: UByte = 0x00u, // Sound Length
    var NR42: UByte = 0x00u, // Volume Envelope
    var NR43: UByte = 0x00u, // Polynomial Counter
    NR44: UByte = 0x00u, // Counter/Consecutive
): Sampler {
    private var lfsr: Int = 0x7FFF // 15-bit LFSR, initialized to all 1s
    private var lengthCounter: Int = 0
    private var envelopeVolume: Int = 0
    private var envelopeTick: Int = 0
    private var cycleCounter: Int = 0
    private var sample: Short = 0
    var isEnabled: Boolean = false

    var NR44: UByte = NR44
        set(value) {
            field = (value and 0x80u) // Only bit 7 is used for trigger
            if (value.bit(7)) {
                trigger()
            }
        }

    private val length by masked(::NR41, 0x3Fu) // 6 bits
    private val lengthEnabled: Boolean
        get() = NR44.bit(6) // Bit 6 indicates if length counter is enabled
    private val initialVolume by masked(::NR42, 0xF0u) // Bits 4-7
    private val envelopeDirection by masked(::NR42, 0x08u) // Bit 3
    private val envelopePeriod
        get() = (NR42 and 0x07u) // Bits 0-2

    private val clockShift by masked(::NR43, 0xF0u, 4) // Bits 4-7
    private val lfsrWidth: UByte
        get() = (NR43 and 0x08u) // Bit 3 indicates if LFSR is 7-bit or 15-bit
    private val clockDiv: UByte
        get() = (NR43 and 0x07u)

    fun trigger() {
        lengthCounter = 64 - length.toInt()
        envelopeVolume = initialVolume.toInt() shr 4 // Use initialVolume from NR42
        envelopeTick = 0
        lfsr = 0x7FFF // Reset LFSR to all 1s
        cycleCounter = 0
        isEnabled = true
    }

    fun onFrameSequencerStep(step: Int) {
        if (step % 2 == 0) {
            if (lengthEnabled && lengthCounter > 0) {
                lengthCounter--
                if (lengthCounter == 0) {
                    isEnabled = false
                }
            }
        }
        if (step == 7 && envelopePeriod != UByte.ZERO) {
            envelopeTick++
            if (envelopeTick >= envelopePeriod.toInt()) {
                envelopeTick = envelopeTick % envelopePeriod.toInt()
                if (envelopeDirection.bit(3)) {
                    if (envelopeVolume < 15) envelopeVolume++
                } else {
                    if (envelopeVolume > 0) envelopeVolume--
                }
            }
        }
    }

    fun update(cycles: Int) {
        if (!isEnabled || lengthCounter <= 0) return
        // Calculate frequency from NR43
        val divisor = when (clockDiv) {
            UByte.ZERO -> 8.0
            else -> clockDiv.toDouble() * 16.0
        }
        val shift = clockShift.toInt().coerceIn(0, 15)
        val freqLong = (524288.0 / (divisor * (2.0.pow(shift + 1)))).toLong()

        val freq = freqLong.coerceAtLeast(1L).toInt() // Clamp to minimum 1

        cycleCounter += cycles
        while (cycleCounter > freq) {
            cycleCounter -= freq

            // LFSR feedback: XOR bit 0 and bit 1
            val bit = ((lfsr and 0x1) xor ((lfsr shr 1) and 0x1))
            lfsr = (lfsr shr 1) or (bit shl 14)
            if (lfsrWidth.bit(3)) {
                // 7-bit LFSR: also copy bit to bit 6
                lfsr = (lfsr and 0xFFBF) or (bit shl 6)
            }
        }
    }

    override fun getSample(range: IntRange): Short {
        if (!isEnabled || lengthCounter <= 0) return 0
        val output = if ((lfsr and 0x1) == 0) envelopeVolume else 0
        return output.scale4BitTo(range)
    }

    fun reset() {
        sample = 0
        lfsr = 0x7FFF
        lengthCounter = 0
        envelopeVolume = 0
        envelopeTick = 0
        cycleCounter = 0
        isEnabled = false
    }
}