package best.william.kgb.audio

interface Sampler {
    fun getSample(range: IntRange = DEFAULT_RANGE): Short

    companion object {
        val DEFAULT_RANGE = Short.MIN_VALUE..Short.MAX_VALUE
    }
}