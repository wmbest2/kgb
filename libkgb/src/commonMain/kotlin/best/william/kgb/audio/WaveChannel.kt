package best.william.kgb.audio

import best.william.kgb.cpu.masked
import kgb.util.bit
import best.william.kgb.util.scale4BitTo

class WaveChannel(
    var NR30: UByte = 0x7Fu, // Sound on/off
    var NR31: UByte = 0xFFu, // Length
    var NR32: UByte = 0x9Fu, // Output level
    var NR33: UByte = 0xFFu, // Frequency low
    NR34: UByte = 0xBFu  // Frequency high / trigger
): Sampler {
    var waveRam = UByteArray(16) // 32 4-bit samples for channel 3 waveform (CPU readable/writable)

    private var sampleIndex = 0
    var isEnabled: Boolean = false // Indicates if the channel is enabled
    private var lengthCounter: Int = 0
    private val lengthEnabled: Boolean
        get() = NR34.bit(7) // Check if sound is enabled


    /**
     * Calculates the combined frequency value from registers.
     */
    val freq: Int
        get() = ((NR34.toInt() and 0x07) shl 8) or (NR33.toInt() and 0xFF)

    private val phaseTracker = PhaseTracker(
        freq = ::freq,
        modulo = 32,
        multiplier = 2 // Each phase corresponds to 2 samples in the wave RAM
    )
    private val outputLevel: Int
        get() {
            return when (NR32.toInt() and 0x60) {
                0x00 -> 0 // Mute
                0x20 -> 1 // 100%
                0x40 -> 2 // 50%
                0x60 -> 3 // 25%
                else -> 1 // Default to 100% if something goes wrong
            }
        }
    private val length by masked(::NR31, 0xFFu) // 8 bits

    var NR34: UByte = NR34
        set(value) {
            field = (value and 0x7Fu) // Ensure the highest bit is always 0
            if (value.bit(7)) {
                // If the highest bit is set, it indicates a trigger event
                trigger()
            }
        }

    fun trigger() {
        isEnabled = NR30.bit(7) // Check if sound is enabled
        lengthCounter = 256 - NR31.toInt()
        sampleIndex = 0
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
    }

    fun update(cycles: Int) {
        if (!isEnabled || lengthCounter <= 0) return
        phaseTracker.update(cycles)
    }

    override fun getSample(range: IntRange): Short {
        if (!isEnabled || lengthCounter <= 0) return 0
        val phase = phaseTracker.currentPhase()
        val sampleIndex = phase / 2 // Each phase corresponds to 2 samples in the wave RAM
        val useHigh = phase % 2 == 0 // Use high nibble for even phases, low for odd
        val sample = if (useHigh) {
            waveRam[sampleIndex].toInt() and 0xF0 shr 4// High nibble
        } else {
            waveRam[sampleIndex].toInt() and 0x0F // Low nibble
        }
        // Output level: 0 = mute, 1 = 100%, 2 = 50%, 3 = 25%
        val level = when (outputLevel) {
            0 -> 0.0
            2 -> 0.5
            3 -> 0.25
            else -> 1.0
        }

        val scaled = (sample.scale4BitTo(range) * level).toInt().coerceIn(range.first, range.last)
        return scaled.toShort()
    }

}