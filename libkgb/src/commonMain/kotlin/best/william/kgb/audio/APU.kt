@file:OptIn(ExperimentalUnsignedTypes::class)

package best.william.kgb.audio

import best.william.kgb.cpu.flag
import best.william.kgb.cpu.masked
import best.william.kgb.util.ShortRingBuffer
import co.touchlab.kermit.Logger
import co.touchlab.kermit.NoTagFormatter
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter
import kgb.memory.IMemory
import kgb.util.bit
import kotlin.reflect.KMutableProperty0


class APU(
    private val speaker: Speaker
) {
    val logger = Logger(
        tag = "APU",
        config = loggerConfigInit(
            minSeverity = co.touchlab.kermit.Severity.Info,
            logWriters = arrayOf(platformLogWriter(NoTagFormatter)),
        )
    )

    init {
        require(speaker.bufferSize % 32 == 0) { "Buffer size must be a multiple of 32" }
        require(speaker.bufferSize > 0) { "Buffer size must be greater than zero" }
    }

    // APU (Audio Processing Unit) is responsible for generating)
    lateinit var memory: IMemory

    // Registers and memory for the APU
    var NR10: UByte = 0x80u // Channel 1 Sweep Register (default: 0x80)
    var NR11: UByte = 0xBFu // Channel 1 Sound (default: 0xBF)
    var NR12: UByte = 0xF3u // Channel 1 Volume Envelope (default: 0xF3)
    var NR13: UByte = 0xFFu // Channel 1 Frequency Low (default: 0xFF)
    var NR14: UByte = 0xBFu // Channel 1 Frequency High / Trigger (default: 0xBF)
    val Channel1DACEnabled by masked(::NR12, 0xF8u) // Bits 3-7 indicate if DAC is enabled
    var Channel1Triggered by flag(::NR14, 0x80) // Bit 7 indicates if channel is triggered

    var NR21: UByte = 0x3Fu // Channel 2 Sound (default: 0x3F)
    var NR22: UByte = 0x00u // Channel 2 Volume (default: 0x00)
    var NR23: UByte = 0xFFu // Channel 2 Frequency Low (default: 0xFF)
    var NR24: UByte = 0xBFu // Channel 2 Frequency High / Trigger (default: 0xBF)
    val Channel2DACEnabled by masked(::NR22, 0xF8u) // Bits 3-7 indicate if DAC is enabled
    val Channel2Triggered by flag(::NR24, 0x80) // Bit 7 indicates if channel is triggered

    var NR30: UByte = 0x7Fu // Channel 3 Sound (default: 0x7F)
    var NR31: UByte = 0xFFu // Channel 3 Length (default: 0xFF)
    var NR32: UByte = 0x9Fu // Channel 3 Select (default: 0x9F)
    var NR33: UByte = 0xFFu // Channel 3 Frequency (default: 0xFF)
    var NR34: UByte = 0xBFu // Channel 3 Frequency (default: 0xBF)
    val Channel3DACEnabled by masked(::NR30, 0x7Fu) // Bit 7 indicates if DAC is enabled
    val Channel3Triggered by flag(::NR34, 0x80) // Bit 7 indicates if channel is triggered

    var NR41: UByte = 0xFFu // Channel 4 Length (default: 0xFF)
    var NR42: UByte = 0x00u // Channel 4 Volume (default: 0x00)
    var NR43: UByte = 0x00u // Channel 4 Polynomial (default: 0x00)
    var NR44: UByte = 0xBFu // Channel 4 Control (default: 0xBF)
    val Channel4DACEnabled by masked(::NR44, 0xBFu) // Bit 7 indicates if DAC is enabled
    val Channel4Triggered by flag(::NR44, 0x80) // Bit 7 indicates if channel is triggered

    var NR50: UByte = 0x7Fu // Master Volume Control (default: 0x7F)
    var NR51: UByte = 0xF3u // Channel control (default: 0xF3)

    var APUEnabled: Boolean = true
        set(value) {
            field = value
            logger.d { "APU enabled: $value" }
            // If APU is disabled, reset all channels
            if (!value) {
                reset()
            }
        }
    var NR52: UByte
        get() {
            if (!APUEnabled) {
                return 0x00u // If APU is disabled, return 0
            } else {
                // Bit 7 indicates if APU is enabled
                // Bits 0-3 indicate if channels 1-4
                val channel1 = if (Channel1DACEnabled != Disabled) 0x01u else 0x00u
                val channel2 = if (Channel2DACEnabled != Disabled) 0x02u else 0x00u
                val channel3 = if (Channel3DACEnabled != Disabled) 0x04u else 0x00u
                val channel4 = if (Channel4DACEnabled != Disabled) 0x08u else 0x00u

                return (0x80u or channel1 or channel2 or channel3 or channel4).toUByte() // Bit 7 set for enabled
            }

        }
        set(value) {
            // Set the APU enabled state based on bit 7
            APUEnabled = value.bit(7)
        }

    // 32 4-bit samples for channel 3 waveform (CPU readable/writable)
    var waveRam = UByteArray(32)

    private val bufferSize: Int
        get() = speaker.bufferSize // Buffer size for audio samples
    private val waveRamSampleBuffer = ShortRingBuffer(speaker.bufferSize) // Buffer for samples generated from wave RAM

    private val cpuFrequency = 4_194_304 // Game Boy CPU frequency in Hz
    private val cyclesPerSample = cpuFrequency / speaker.sampleOutputRate.hertz // CPU cycles per audio sample
    private val cyclesPerLatch = cyclesPerSample * 32 // Latch every 32 samples
    private val latchesPerBuffer = bufferSize / 32

    private var latchAccumulator = 0
    private var latchCount = 0

    private var wavePhase = 0.0 // Persistent phase for channel 3 waveform
    private var square1Phase = 0.0
    private var square2Phase = 0.0

    fun generateSquareWave(frequency: Int, duty: Int, volume: Int, phaseRef: KMutableProperty0<Double>): ShortArray {
        val samples = ShortArray(bufferSize) { 0}

        // Validate inputs
        if (volume == 0 || frequency >= 2047) {
            phaseRef.set(0.0) // Reset phase when silent
            return samples // Return silence
        }

        logger.d {
            "Generating square wave: frequency=$frequency, duty=$duty, volume=$volume, bufferSize=$bufferSize, currentPhase=${phaseRef.get()}"
        }

        // Game Boy square wave period in CPU cycles
        val gbPeriodCycles = (2048 - frequency) * 4

        val periodInSeconds = gbPeriodCycles / cpuFrequency.toDouble()
        val periodInSamples = periodInSeconds * speaker.sampleOutputRate.hertz

        val pattern = dutyPatterns[duty and 0x3]
        val amplitude = volume * (Short.MAX_VALUE / 15) // Scale volume to 16-bit signed short range

        var phase = phaseRef.get()
        for (i in samples.indices) {
            val positionInPeriod = phase / periodInSamples
            val dutyStep = (positionInPeriod % 8).toInt()
            val output = if (pattern[dutyStep] == 1) amplitude else 0
            samples[i] = output.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            phase += 1.0
        }
        phaseRef.set(phase)
        return samples
    }

    private fun getDutyFraction(duty: Int): Double {
        // Game Boy duty cycles: 12.5%, 25%, 50%, 75%
        return when (duty and 0x3) {
            0 -> 0.125
            1 -> 0.25
            2 -> 0.5
            3 -> 0.75
            else -> 0.5
        }
    }


    fun generateNoise(frequency: Int, volume: Int): ShortArray {
        // Generate a noise sample based on frequency and volume
        val samples = ShortArray(bufferSize)
        for (i in samples.indices) {
            // Simple noise generation logic (stubbed)
            samples[i] = if (frequency != 0 && i % frequency == 0) (volume * Short.MAX_VALUE / 15).toShort() else 0
        }
        return samples
    }

    fun generateWaveSample(frequency: Int, volume: Int) {
        // Generate a wave sample based on frequency and volume
        val phaseIncrement = frequency.toDouble() / speaker.sampleOutputRate.hertz
        for (i in waveRam.indices) {
            // Simple wave generation logic (stubbed)
            val output = if ((wavePhase + i * phaseIncrement) % 1.0 < 0.5) (volume * Short.MAX_VALUE / 15).toShort() else 0
            waveRamSampleBuffer.add(output)
        }
        wavePhase += phaseIncrement * bufferSize
        if (wavePhase >= 1.0) wavePhase -= 1.0
    }

    fun updateChannel1(): ShortArray {
        // Only check if channel 1 is enabled (bit 0 of NR52)
        if (Channel1DACEnabled == Disabled) {
            logger.d { "Channel 1 is disabled, outputting silence." }
            // Channel is not enabled, output silence
            return ShortArray(bufferSize) { 0 }
        }
        if (Channel1Triggered) {

            Channel1Triggered = false // Reset trigger state after processing
        }
        // Frequency is 11 bits: lower 8 from NR13, upper 3 from NR14
        val freqRaw = getFrequency(NR13, NR14)
        val duty = getDuty(NR11)
        val volume = getEnvelopeVolume(NR12)
        return generateSquareWave(freqRaw, duty, volume, ::square1Phase)
    }

    fun updateChannel2(): ShortArray {
        val frequency = getFrequency(NR23, NR24)
        val duty = getDuty(NR21)
        val volume = getEnvelopeVolume(NR22)
        return generateSquareWave(frequency, duty, volume, ::square2Phase)
    }

    /**
     * This renders the wave sample into the waveRamSampleBuffer 32 samples at a time.
     * It is called when the waveRam is latched, and it generates the samples based
     * on the current waveRam state.
     * The waveRamSampleBuffer is then used to generate audio samples for channel 3.
     */
    fun latchWaveRam() {
        // Update channel 3 state and generate audio
        val frequency = getFrequency(NR33, NR34)
        val volume = getWaveOutputLevel(NR32)
        generateWaveSample(frequency, volume)
    }

    fun updateChannel4(): ShortArray {
        // Update channel 4 state and generate audio
        val frequency = getNoiseFrequency(NR43)
        val volume = getEnvelopeVolume(NR42)
        return generateNoise(frequency, volume)
    }

    fun update(cycles: Int): Int {
        if (!APUEnabled) {
            // APU is disabled, no audio generation
            return 0
        }
        // Accumulate cycles and generate audio only when enough cycles have passed for a buffer
        latchAccumulator += cycles
        var samplesToGenerate = 0

        // We use latching because we have to lock in waveRam samples,
        // and we want to ensure we generate a consistent number of samples per buffer
        // Calculate how many latches we can process
        while (latchAccumulator >= cyclesPerLatch) {
            latchAccumulator -= cyclesPerLatch
            latchCount++

            latchWaveRam()

            if (latchCount == latchesPerBuffer) {
                latchCount = 0
                samplesToGenerate++
            }
        }

        for( i in 0 until samplesToGenerate) {

            logger.d { "Generating audio samples: $i/$samplesToGenerate"}
            val emptyArray = ShortArray(bufferSize)
            val channel1 = if (true) updateChannel1() else emptyArray // Update channel 1
            val channel2 = if (true) updateChannel2() else emptyArray // Update channel 2
            val channel3 = if (true) waveRamSampleBuffer.copyOf() else emptyArray // Read from waveRamSampleBuffer
            val channel4 = if (true) updateChannel4() else emptyArray // Update channel 4

            speaker.play(
                channel1,
                channel2,
                channel3,
                channel4
            )
        }

        return samplesToGenerate
    }

    // Helper functions (stubbed for illustration)
    private fun getFrequency(low: UByte, high: UByte): Int {
        // Combine low and high register values to get frequency
        return ((high.toInt() and 0x07) shl 8) or low.toInt()
    }
    private fun getDuty(nr: UByte): Int {
        // Extract duty cycle from register
        return (nr.toInt() shr 6) and 0x03
    }
    private fun getEnvelopeVolume(nr: UByte): Int {
        // Extract initial envelope volume
        return (nr.toInt() shr 4) and 0x0F
    }

    private fun getWaveOutputLevel(nr: UByte): Int {
        // Extract output level from register
        return (nr.toInt() shr 5) and 0x03
    }
    private fun getNoiseFrequency(nr: UByte): Int {
        // Extract noise frequency from polynomial register
        return nr.toInt() and 0x1F
    }

    fun reset() {
        // Reset all registers to default values
        NR10 = 0x80u
        NR11 = 0xBFu
        NR12 = 0xF3u
        NR13 = 0xFFu
        NR14 = 0xBFu

        NR21 = 0x3Fu
        NR22 = 0x00u
        NR23 = 0xFFu
        NR24 = 0xBFu
        NR30 = 0x7Fu

        NR31 = 0xFFu
        NR32 = 0x9Fu
        NR33 = 0xFFu
        NR34 = 0xBFu

        NR41 = 0xFFu
        NR42 = 0x00u
        NR43 = 0x00u
        NR44 = 0xBFu

        NR50 = 0x77u
        NR51 = 0xF3u
        NR52 = 0xF1u

        waveRam.fill(0u)

        waveRamSampleBuffer.clear()

        latchAccumulator = 0
        latchCount = 0

        wavePhase = 0.0
        square1Phase = 0.0
        square2Phase = 0.0
    }

    companion object {
        private val Disabled = 0x0u.toUByte()

        val dutyPatterns = arrayOf(
            intArrayOf(0,0,0,0,0,0,0,1), // 12.5%
            intArrayOf(1,0,0,0,0,0,0,1), // 25%
            intArrayOf(1,0,0,0,0,1,1,1), // 50%
            intArrayOf(0,1,1,1,1,1,1,0)  // 75%
        )
    }
}
