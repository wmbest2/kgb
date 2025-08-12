@file:OptIn(ExperimentalTime::class)

package best.william.kgb.audio

import best.william.kgb.cpu.masked
import co.touchlab.kermit.Logger
import co.touchlab.kermit.loggerConfigInit
import kgb.util.ZERO
import kgb.util.bit
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

open class PulseChannel(

    /**
     * Channel 1 Sound (default: 0xBF)
     * The sound channel controls the sound output for the pulse channel.
     * Bit Breakdown:
     * - Bit 7: Enabled (0 = Disabled, 1 = Enabled)
     * - Bits 6-4: Duty Cycle (0-7, determines the waveform shape)
     * - Bits 3-0: Length Counter (0-15, determines the length of the sound)
     */
    DutyLength: UByte = 0xBFu, // Channel 1 Sound (default: 0xBF)
    /**
     * Channel 1 Volume Envelope (default: 0xF3)
     * The volume envelope controls the volume of the sound channel.
     * Bit Breakdown:
     * - Bit 7: Envelope Direction (0 = Decrease, 1 = Increase
     * - Bits 6-4: Envelope Period (0-7, determines how quickly the volume changes)
     * - Bits 3-0: Initial Volume (0-15, determines the starting volume level)
     */
    VolumeEnvelope: UByte = 0xF3u, // Channel 1 Volume Envelope (default: 0xF3)
    /**
     * Channel 1 Frequency Low (default: 0xFF)
     * The frequency low byte determines the lower part of the frequency for the sound channel.
     * It is used in conjunction with the frequency high byte to calculate the actual frequency.
     */
    var FreqLow: UByte = 0xFFu, // Channel 1 Frequency Low (default: 0xFF)
    /**
     * Channel 1 Frequency High / Trigger (default: 0xBF)
     * The frequency high byte determines the upper part of the frequency for the sound channel.
     * It also serves as a trigger for the sound channel when the highest bit is set.
     * Bit Breakdown:
     * - Bits 6-0: Frequency High (0-127, determines the upper part of the frequency)
     * - Bit 7: Trigger (0 = No trigger, 1 = Trigger sound)
     */
    FreqHigh: UByte = 0xBFu, // Channel 1 Frequency High / Trigger (default: 0xBF)
    logTag: String? = null,
    logLevel: co.touchlab.kermit.Severity = co.touchlab.kermit.Severity.Debug
): Sampler {
    private val logger = Logger(
        tag = "PulseChannel",
        config = loggerConfigInit(
            minSeverity = logLevel,
            logWriters = arrayOf(co.touchlab.kermit.platformLogWriter())
        )
    )
    var isEnabled: Boolean = true
        set(value) {
            field = value
            if (!value) {
                logger.d { "PulseChannel disabled" }
            } else {
                logger.d { "PulseChannel enabled" }
            }
        }

    var DutyLength: UByte = DutyLength
        set(value) {
            field = value
            lengthCounter = 64 - (value and 0x3Fu).toInt() // Length counter uses 6 bits (0-63), so max is 64-0=64, min is 64-63=1
            logger.d { "Reloading length counter: $lengthCounter" }
        }
    var VolumeEnvelope: UByte = VolumeEnvelope
        set(value) {
            field = value
            if ((value and 0xF8u) == UByte.ZERO){
                logger.d { "VolumeEnvelope set to zero, disabling channel" }
                isEnabled = false
            }
        }

    private val dacEnabled by masked(::VolumeEnvelope, 0xF8u) // Bits 7-3 for DAC enabled (0 = Disabled, 1 = Enabled)

    private val initialVolume by masked(::VolumeEnvelope, 0xF0u) // Bits 4-7 for initial volume (0-15)
    private val envelopeDirection by masked(::VolumeEnvelope, 0x08u) // Bit 3 for envelope direction (0 = decrease, 1 = increase)
    private val envelopePeriod by masked(::VolumeEnvelope, 0x07u) // Bits  for envelope period
    private val lengthCounterEnabled: Boolean
        get() = FreqHigh.bit(6) // Bit 7 indicates if length counter is enabled (0 = Disabled, 1 = Enabled)

    private val dutyCycle: UByte
        get() = (DutyLength and 0xC0u) // Bits 6-7 for duty cycle (0-3)
    private val length: UByte
        get() = (DutyLength and 0x3Fu) // Bits 0-5 for length counter (0-63)


    /**
     * Calculates the combined frequency value from registers.
     */
    val freq: Int
        get() = ((FreqHigh.toInt() and 0x07) shl 8) or (FreqLow.toInt() and 0xFF)

    private var phaseTracker: PhaseTracker = PhaseTracker(
        freq = ::freq,
        modulo = 8 // Pulse channels have a modulo of 8 for phase tracking
    )

    var FreqHigh: UByte = FreqHigh
        set(value) {
            field = (value and 0x7Fu) // Ensure the highest bit is always 0
            if (value.bit(7)) {
                // If the highest bit is set, it indicates a trigger event
                trigger()
            }
        }

    val frequency: Int
        get() = ((FreqHigh.toInt() and 0x7F) shl 8) or (FreqLow.toInt() and 0xFF)

    private var lengthCounter: Int = 0

    private var envelopeVolume: Int = (VolumeEnvelope.toInt() shr 4) and 0xF
    private var envelopeTick: Int = 0

    open fun trigger() {
        lengthCounter = 64 - length.toInt() // Length counter uses 6 bits (0-63), so max is 64-0=64, min is 64-63=1

        phaseTracker.reset()
        envelopeVolume = (VolumeEnvelope.toInt() shr 4) and 0xF // 0-15
        envelopeTick = 0

        isEnabled = dacEnabled != UByte.ZERO && envelopeVolume > 0 && frequency != 0
        logger.d { "PulseChannel triggered: isEnabled=$isEnabled, lengthCounter=$lengthCounter, envelopeVolume=$envelopeVolume, frequency=$frequency" }
    }

    @OptIn(ExperimentalTime::class)
    open fun onFrameSequencerStep(step: Int) {
        if (step % 2 == 0) {
            if (lengthCounterEnabled && lengthCounter > 0) {
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
                    if (envelopeVolume < 15) {
                        envelopeVolume++
                    }
                } else {
                    if (envelopeVolume > 0) {
                        envelopeVolume--
                    }
                }
            }
        }
    }

    /** This function is called every cycle to update the channel state.
     * It should handle the countdown, volume, duty cycle, frequency, and length.
     *
     * @param cycles The number of cycles since the last update.
     */
    open fun update(cycles: Int) {
        phaseTracker.update(cycles)
    }

    override fun getSample(range: IntRange): Short {
        if (!isEnabled || lengthCounter <= 0) {
            return 0
        }

        // Use current envelope volume (implement envelope logic elsewhere)
        val volume = envelopeVolume.toDouble() / 15.0

        val dutyIndex = (dutyCycle.toInt() shr 6) and 0x03
        val pattern = dutyCycleValues[dutyIndex]
        val output = ((pattern shr (7 - phaseTracker.currentPhase())) and 1u).toInt()
        val sample = if (output == 1) range.last else range.first

        return (sample * volume).toInt().toShort()
    }

    companion object {
        val dutyCycleValues: Array<UInt> = arrayOf(
            0b00000001u, // 12.5% duty cycle
            0b10000001u, // 25% duty cycle
            0b10000111u, // 50% duty cycle
            0b01111110u,  // 75% duty cycle
        )
    }
}

class PulseSweepChannel(
    var Sweep: UByte = 0x80u,
    DutyLength: UByte = 0xBFu,
    VolumeEnvelope: UByte = 0xF3u,
    FreqLow: UByte = 0xFFu,
    FreqHigh: UByte = 0xBFu,
    logTag: String? = null,
    logLevel: co.touchlab.kermit.Severity = co.touchlab.kermit.Severity.Debug
) : PulseChannel(  DutyLength, VolumeEnvelope, FreqLow, FreqHigh, logTag, logLevel) {
    
    private var shadowFrequency: Int = 0
    private var sweepTimer: Int = 0

    var sweepEnabled: Boolean = false
    var sweepTime: Int = 0
    var sweepShift: Int = 0
    var sweepIncrease: Boolean = false

    override fun trigger() {
        super.trigger()
        sweepTime = 0
        sweepShift = (Sweep and 0x07u).toInt()
        sweepIncrease = !Sweep.bit(3) // 0 = increase, 1 = decrease (hardware logic)
        sweepEnabled = Sweep.bit(6) || sweepShift != 0 // Enable if sweep time or shift is nonzero
        shadowFrequency = ((FreqHigh.toInt() and 0x7) shl 8) or (FreqLow.toInt() and 0xFF)
        sweepTimer = ((Sweep.toInt() shr 4) and 0x7)
        if (sweepTimer == 0) sweepTimer = 8
        if (sweepShift > 0) {
            val newFreq = calculateSweepFrequency()
            if (newFreq > 2047) isEnabled = false
        }
    }

    override fun onFrameSequencerStep(step: Int) {
        super.onFrameSequencerStep(step)
        if (step == 2 || step == 6) {
            if (sweepEnabled && sweepTimer > 0) {
                sweepTimer--
                if (sweepTimer == 0) {
                    sweepTimer = ((Sweep.toInt() shr 4) and 0x7)
                    if (sweepTimer == 0) sweepTimer = 8
                    val newFreq = calculateSweepFrequency()
                    if (newFreq <= 2047 && sweepShift > 0) {
                        shadowFrequency = newFreq
                        FreqLow = (newFreq and 0xFF).toUByte()
                        FreqHigh = ((FreqHigh.toInt() and 0xF8) or ((newFreq shr 8) and 0x7)).toUByte()
                        // Second overflow check
                        if (calculateSweepFrequency() > 2047) isEnabled = false
                    } else if (newFreq > 2047) {
                        isEnabled = false
                    }
                }
            }
        }
    }

    private fun calculateSweepFrequency(): Int {
        val delta = shadowFrequency shr sweepShift
        return if (sweepIncrease) shadowFrequency + delta else shadowFrequency - delta
    }
}
