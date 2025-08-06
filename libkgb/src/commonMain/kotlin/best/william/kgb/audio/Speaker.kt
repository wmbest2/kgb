package best.william.kgb.audio

import kotlin.jvm.JvmInline

@JvmInline
value class SampleRate(val hertz: Int)

interface Speaker {
    /**
     * Gameboy sound specification:

     */

    val sampleOutputRate: SampleRate
        get() = SampleRate(48_000)

    val bufferSize: Int
        get() = 1024

    fun play(channel1: ShortArray, channel2: ShortArray, channel3: ShortArray, channel4: ShortArray)
}