/*
package best.william.kgb.speaker

class APU(
    private val speaker: Speaker
) {
    // APU (Audio Processing Unit) is responsible for generating)

    // Registers and memory for the APU
    private val NR10: UByte = 0x80u // Channel 1 Sweep Register (default: 0x80)
    private val NR11: UByte = 0xBFu // Channel 1 Sound (default: 0xBF)
    private val NR12: UByte = 0xF3u // Channel 1 Volume Envelope (default: 0xF3)
    private val NR13: UByte = 0xFFu // Channel 1 Frequency Low (default: 0xFF)
    private val NR14: UByte = 0xBFu // Channel 1 Frequency High / Trigger (default: 0xBF)

    private val NR21: UByte = 0x3Fu // Channel 2 Sound (default: 0x3F)
    private val NR22: UByte = 0x00u // Channel 2 Volume (default: 0x00)
    private val NR23: UByte = 0xFFu // Channel 2 Frequency Low (default: 0xFF)
    private val NR24: UByte = 0xBFu // Channel 2 Frequency High / Trigger (default: 0xBF)
    private val NR30: UByte = 0x7Fu // Channel 3 Sound (default: 0x7F)

    private val NR31: UByte = 0xFFu // Channel 3 Length (default: 0xFF)
    private val NR32: UByte = 0x9Fu // Channel 3 Select (default: 0x9F)
    private val NR33: UByte = 0xFFu // Channel 3 Frequency (default: 0xFF)
    private val NR34: UByte = 0xBFu // Channel 3 Frequency (default: 0xBF)

    private val NR41: UByte = 0xFFu // Channel 4 Length (default: 0xFF)
    private val NR42: UByte = 0x00u // Channel 4 Volume (default: 0x00)
    private val NR43: UByte = 0x00u // Channel 4 Polynomial (default: 0x00)
    private val NR44: UByte = 0xBFu // Channel 4 Control (default: 0xBF)

    private val NR50: UByte = 0x77u // Master Volume (default: 0x77)
    private val NR51: UByte = 0xF3u // Channel Control (default: 0xF3)
    private val NR52: UByte = 0xF1u // APU Control (default: 0xF1)

    val channel1SampleRingBuffer = ShortArray(256) // Example buffer for channel 1
    val channel2SampleRingBuffer = ShortArray(256) // Example buffer for channel 2
    val channel3SampleRingBuffer = ShortArray(256) // Example buffer for channel 3
    val channel4SampleRingBuffer = ShortArray(256) // Example buffer

    fun update(cycles: Int) {
        // Example implementation: advance APU state and generate audio
        // These are simplified and illustrative; real emulation would be more complex

        // Advance length counters, envelope, sweep, etc. (stubbed)
        // For each channel, generate a sample and send to speaker


        speaker.playSamples(
            channel1Samples = channel1Sample,
            channel2Samples = channel2SampleRingBuffer,
            channel3Samples = channel3SampleRingBuffer,
            channel4Samples = channel4SampleRingBuffer
        )

        // Channel 1: Square wave with sweep/envelope
        speaker.playSquareWave(
            channel = 1,
            frequency = getFrequency(NR13, NR14),
            duty = getDuty(NR11),
            volume = getEnvelopeVolume(NR12)
        )
        // Channel 2: Square wave
        speaker.playSquareWave(
            channel = 2,
            frequency = getFrequency(NR23, NR24),
            duty = getDuty(NR21),
            volume = getEnvelopeVolume(NR22)
        )
        // Channel 3: Programmable waveform
        speaker.playAudioSample(
            channel = 3,
            samples = getWaveRamSamples(),
            frequency = getFrequency(NR33, NR34),
            volume = getWaveOutputLevel(NR32)
        )
        // Channel 4: Noise
        speaker.playNoise(
            channel = 4,
            frequency = getNoiseFrequency(NR43),
            volume = getEnvelopeVolume(NR42)
        )
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
    private fun getWaveRamSamples(): ShortArray {
        // Return 32 4-bit samples (stub)
        return ShortArray(32) { 0 }
    }
    private fun getWaveOutputLevel(nr: UByte): Int {
        // Extract output level from register
        return (nr.toInt() shr 5) and 0x03
    }
    private fun getNoiseFrequency(nr: UByte): Int {
        // Extract noise frequency from polynomial register
        return nr.toInt() and 0x1F
    }
}*/
