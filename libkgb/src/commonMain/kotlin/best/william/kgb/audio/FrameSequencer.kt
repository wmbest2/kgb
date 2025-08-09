package best.william.kgb.audio

typealias OnFrameSequencerStep = (step: Int) -> Unit

class FrameSequencer(
    private val steps: Int = 8,
    private val cyclesPerStep: Int = 8192,
    private val onStep: OnFrameSequencerStep = { step: Int -> } // Default no-op function
) {
    private var cycleCounter = 0
    private var currentStep = 0

    fun tick(cycles: Int) {
        cycleCounter += cycles
        while (cycleCounter >= cyclesPerStep) {
            cycleCounter -= cyclesPerStep
            onStep(currentStep)
            currentStep = (currentStep + 1) % steps
        }
    }

    fun reset() {
        cycleCounter = 0
        currentStep = 0
    }
}