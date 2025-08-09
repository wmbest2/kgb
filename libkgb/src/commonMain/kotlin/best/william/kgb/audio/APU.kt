@file:OptIn(ExperimentalUnsignedTypes::class)

package best.william.kgb.audio

import co.touchlab.kermit.Logger
import co.touchlab.kermit.NoTagFormatter
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter
import kgb.memory.IMemory
import kgb.util.bit
import kotlin.UByte
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KMutableProperty0


class APU(
    private val speaker: Speaker
) {
    val logger = Logger(
        tag = "APU",
        config = loggerConfigInit(
            minSeverity = co.touchlab.kermit.Severity.Debug,
            logWriters = arrayOf(platformLogWriter(NoTagFormatter)),
        )
    )

    init {
        require(speaker.bufferSize > 0) { "Buffer size must be greater than zero" }
    }

    private val cpuFrequency = 4_194_304 // Game Boy CPU frequency in Hz
    private val cyclesPerSample = cpuFrequency / speaker.sampleOutputRate.hertz // CPU cycles per audio sample


    // APU (Audio Processing Unit) is responsible for generating)
    lateinit var memory: IMemory

    // Registers and memory for the APU
    val channel1 = PulseSweepChannel(
        Sweep = 0x80u, // Default sweep value for channel 1
        DutyLength = 0xBFu, // Default duty length for channel 1
        VolumeEnvelope = 0xF3u, // Default volume for channel 1
        FreqLow = 0xFFu, // Default frequency low for channel 1
        FreqHigh = 0xBFu, // Default frequency high for channel 1
        logTag = "PulseChannel1",
        logLevel = co.touchlab.kermit.Severity.Debug
    )

    val channel2 = PulseChannel(
        DutyLength = 0x3Fu, // Default duty length for channel 2
        VolumeEnvelope = 0x00u, // Default volume for channel 2
        FreqLow = 0xFFu, // Default frequency low for channel 2
        FreqHigh = 0xBFu, // Default frequency high for channel 2
        logTag = "PulseChannel2",
        logLevel = co.touchlab.kermit.Severity.Warn
    )


    val channel3 = WaveChannel(
        NR30 = 0x7Fu, // Channel 3 Sound (default: 0x7F)
        NR31 = 0xFFu, // Channel 3 Length (default: 0xFF)
        NR32 = 0x9Fu, // Channel 3 Select (default: 0x9F)
        NR33 = 0xFFu, // Channel 3 Frequency (default: 0xFF)
        NR34 = 0xBFu, // Channel 3 Frequency (default: 0xBF)
    )

    val channel4 = NoiseChannel(
        NR41 = 0xFFu, // Channel 4 Length (default: 0xFF)
        NR42 = 0x00u, // Channel 4 Volume Envelope (default: 0x00)
        NR43 = 0x00u, // Channel 4 Polynomial Counter (default: 0x00)
        NR44 = 0xBFu, // Channel 4 Counter/Consecutive (default: 0xBF)
    )

    val frameSequencer = FrameSequencer(onStep = { step ->
        channel1.onFrameSequencerStep(step)
        channel2.onFrameSequencerStep(step)
        channel3.onFrameSequencerStep(step)
        channel4.onFrameSequencerStep(step)
    })


    var NR10: UByte by apuRegister(channel1::Sweep) // Channel 1 Sweep Register (default: 0x80)
    var NR11: UByte by apuRegister(channel1::DutyLength) // Channel 1 Duty Length Register (default: 0xBF)
    var NR12: UByte by apuRegister(channel1::VolumeEnvelope) // Channel 1 Volume Envelope (default: 0xF3)
    var NR13: UByte by apuRegister(channel1::FreqLow) // Channel 1 Frequency Low (default: 0xFF)
    var NR14: UByte by apuRegister(channel1::FreqHigh) // Channel 1 Frequency High / Trigger (default: 0xBF)

    var NR21: UByte by apuRegister(channel2::DutyLength) // Channel 2 Duty Length Register (default: 0x3F)
    var NR22: UByte by apuRegister(channel2::VolumeEnvelope) // Channel 2 Volume Envelope (default: 0x00)
    var NR23: UByte by apuRegister(channel2::FreqLow) // Channel
    var NR24: UByte by apuRegister(channel2::FreqHigh) // Channel 2 Frequency High / Trigger (default: 0xBF)

    var NR30: UByte by apuRegister(channel3::NR30)
    var NR31: UByte by apuRegister(channel3::NR31)
    var NR32: UByte by apuRegister(channel3::NR32)
    var NR33: UByte by apuRegister(channel3::NR33)
    var NR34: UByte by apuRegister(channel3::NR34)

    var NR41: UByte by apuRegister(channel4::NR41) // Channel 4 Length (default: 0xFF)
    var NR42: UByte by apuRegister(channel4::NR42) // Channel 4 Volume Envelope (default: 0x00)
    var NR43: UByte by apuRegister(channel4::NR43) // Channel 4 Polynomial Counter (default: 0x00)
    var NR44: UByte by apuRegister(channel4::NR44) // Channel 4 Counter/Consecutive (default: 0xBF)

    // Bit 7 indicates if DAC is enabled

    var NR50: UByte = 0x7Fu // Master Volume Control (default: 0x7F)
    var NR51: UByte = 0xF3u // Channel control (default: 0xF3)

    private var cycleCounter = 0 // Cycle counter for APU operations

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
                val channel1 = if (channel1.isEnabled) 0x01u else 0x00u
                val channel2 = if (channel2.isEnabled) 0x02u else 0x00u
                val channel3 = if (channel3.isEnabled) 0x04u else 0x00u
                val channel4 = if (channel4.isEnabled) 0x08u else 0x00u

                return (0x80u or channel1 or channel2 or channel3 or channel4).toUByte() // Bit 7 set for enabled
            }

        }
        set(value) {
            // Set the APU enabled state based on bit 7
            APUEnabled = value.bit(7)
        }

    val waveRam by channel3::waveRam

    private val bufferSize: Int = speaker.bufferSize // Buffer size for audio samples

    var sampleIndex = 0 // Current sample index
    val channel1Buffer: ShortArray = ShortArray(speaker.bufferSize) // Buffer for channel 1 samples
    val channel2Buffer: ShortArray = ShortArray(speaker.bufferSize)
    val channel3Buffer: ShortArray = ShortArray(speaker.bufferSize)
    val channel4Buffer: ShortArray = ShortArray(speaker.bufferSize)

    fun update(cycles: Int): Int {
        if (!APUEnabled) {
            return 0
        }
        frameSequencer.tick(cycles)
        channel1.update(cycles)
        channel2.update(cycles)
        channel3.update(cycles)
        channel4.update(cycles)

        cycleCounter += cycles


        while (cycleCounter >= cyclesPerSample) {
            cycleCounter -= cyclesPerSample

            channel1Buffer[sampleIndex] = channel1.getSample(Sampler.DEFAULT_RANGE)
            channel2Buffer[sampleIndex] = channel2.getSample(Sampler.DEFAULT_RANGE)
            channel3Buffer[sampleIndex] = channel3.getSample(Sampler.DEFAULT_RANGE)
            channel4Buffer[sampleIndex] = channel4.getSample(Sampler.DEFAULT_RANGE)

            sampleIndex = (sampleIndex + 1) % bufferSize

            if (sampleIndex == 0) { // Play when buffer wraps around (buffer is full)
                speaker.play(
                    channel1Buffer,
                    channel2Buffer,
                    channel3Buffer,
                    channel4Buffer,
                )
            }
        }


        return 0
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
    }

    fun apuRegister(register: KMutableProperty0<UByte>): ReadWriteProperty<APU, UByte> {
        return object : ReadWriteProperty<APU, UByte> {
            override fun getValue(thisRef: APU, property: kotlin.reflect.KProperty<*>): UByte {
                if (!thisRef.APUEnabled) {
                    thisRef.logger.w { "APU is disabled, returning 0 for register ${property.name}" }
                    return 0u // If APU is disabled, return 0
                }
                return register.get()
            }

            override fun setValue(thisRef: APU, property: kotlin.reflect.KProperty<*>, value: UByte) {
                if (!thisRef.APUEnabled) {
                    thisRef.logger.w { "APU is disabled, ignoring write to register ${property.name}" }
                    return // If APU is disabled, ignore writes
                }
                register.set(value)
            }
        }
    }

    object NullSpeaker : Speaker {
        override val bufferSize: Int = 512
        override val sampleOutputRate: SampleRate = SampleRate(44100)

        override fun play(channel1: ShortArray, channel2: ShortArray, channel3: ShortArray, channel4: ShortArray) {
            // No-op for null speaker
        }
    }
}
