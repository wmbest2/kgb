package best.william.kgb.audio

import org.lwjgl.openal.AL
import org.lwjgl.openal.ALC
import org.lwjgl.openal.AL10.*
import org.lwjgl.openal.ALC10
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.MemoryUtil.memAllocShort
import org.lwjgl.system.MemoryUtil.memFree

class OpenALSpeaker(): Speaker {
    private val device = ALC10.alcOpenDevice(null as String?)
    private val context = ALC10.alcCreateContext(device, null as IntArray?)
    private var source = 0
    private val NUM_BUFFERS = 4
    override val bufferSize: Int = 4096
    private val mixerBuffer = ShortArray(bufferSize) // Buffer for mixing audio channels
    private val buffers = IntArray(NUM_BUFFERS)
    private var nextBuffer = 0
    private var initialized = false

    private var hpLastInput: Short = 0
    private var hpLastOutput: Short = 0

    init {
        if (device == MemoryUtil.NULL) throw IllegalStateException("Failed to open OpenAL device")
        ALC10.alcMakeContextCurrent(context)
        AL.createCapabilities(ALC.createCapabilities(device))
        source = alGenSources()
        alGenBuffers(buffers)
        initialized = true
    }


    override fun play(
        channel1: ShortArray,
        channel2: ShortArray,
        channel3: ShortArray,
        channel4: ShortArray
    ) {
        if (!initialized) return
        if (channel1.size != bufferSize || channel2.size != bufferSize || channel3.size != bufferSize || channel4.size != bufferSize) {
            println("All channels must be exactly $bufferSize samples!")
            return
        }

        // Mix the 4 channels into a single mono buffer
        for (i in mixerBuffer.indices) {
            val c1 = channel1[i]
            val c2 = channel2[i]
            val c3 = channel3[i]
            val c4 = channel4[i]
            mixerBuffer[i] = ((c1 + c2 + c3 + c4) / 4).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }

/*        // Apply a simple low-pass filter
        var lastOutput = hpLastOutput
        val alpha = 0.3f // Low-pass filter coefficient
        for (i in mixerBuffer.indices) {
            val input = mixerBuffer[i]
            val output = (alpha * input + (1 - alpha) * lastOutput).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            mixerBuffer[i] = output
            lastOutput = output
        }
        hpLastOutput = lastOutput*/

        // Prepare OpenAL buffer
        val buf = memAllocShort(bufferSize)
        buf.put(mixerBuffer).flip()
        try {
            // Unqueue processed buffers so we can reuse them
            val processed = alGetSourcei(source, AL_BUFFERS_PROCESSED)
            if (processed > 0) {
                val unqueued = IntArray(processed)
                alSourceUnqueueBuffers(source, unqueued)
            }

            // Fill the next buffer in the ring
            val bufferId = buffers[nextBuffer]
            alBufferData(bufferId, AL_FORMAT_MONO16, buf, sampleOutputRate.hertz)
            val errorBuffer = alGetError()
            if (errorBuffer != AL_NO_ERROR) {
                println("OpenAL error during buffer data: $errorBuffer")
                return
            }

            alSourceQueueBuffers(source, intArrayOf(bufferId))
            nextBuffer = (nextBuffer + 1) % NUM_BUFFERS

            // Start playback if not already playing
            val state = alGetSourcei(source, AL_SOURCE_STATE)
            if (state != AL_PLAYING) {
                alSourcePlay(source)
            }
        } finally {
            memFree(buf)
        }
    }

    protected fun finalize() {
        alDeleteSources(source)
        ALC10.alcDestroyContext(context)
        ALC10.alcCloseDevice(device)
    }
}