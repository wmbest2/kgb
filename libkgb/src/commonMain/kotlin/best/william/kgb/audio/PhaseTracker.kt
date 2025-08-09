package best.william.kgb.audio

import kotlin.reflect.KProperty0

/**
 * Tracks phase advancement for Game Boy audio channels.
 * Handles phase counter, phase, and frequency register logic.
 */
class PhaseTracker(
    var freqLow: KProperty0<UByte>,
    var freqHigh: KProperty0<UByte>,
    private val modulo: Int,
    private val multiplier: Int = 4,
) {
    private var phaseCounter: Int = initialPhaseCounter()
    var phase: Int = 0
        private set
    /**
     * Calculates the combined frequency value from registers.
     */
    val freq: Int
        get() = ((freqHigh().toInt() and 0x07) shl 8) or (freqLow().toInt() and 0xFF)

    /**
     * Resets the phase counter and phase based on current frequency.
     */
    fun reset() {
        phaseCounter = initialPhaseCounter()
        phase = 0
    }

    /**
     * Advances the phase based on cycles, wrapping at modulo.
     */
    fun update(cycles: Int) {
        phaseCounter -= cycles
        while (phaseCounter <= 0) {
            val period = multiplier * (2048 - freq) // Recalculate period based on current frequency
            phaseCounter += period
            phase = (phase + 1) % modulo
        }
    }

    /**
     * Returns the current phase index.
     */
    fun currentPhase(): Int = phase

    /**
     * Helper to calculate initial phase counter from frequency.
     */
    private fun initialPhaseCounter(): Int = multiplier * (2048 - freq)
}
