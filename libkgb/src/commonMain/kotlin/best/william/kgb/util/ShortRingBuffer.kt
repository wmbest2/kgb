package best.william.kgb.util

class ShortRingBuffer(size: Int) {

    private val internalStorage: ShortArray = ShortArray(size)

    var writeIndex = 0
        private set

    init {
        require(size > 0) { "Size must be greater than zero." }
    }

    operator fun plus(element: Short) {
        internalStorage[writeIndex] = element
        writeIndex = (writeIndex + 1) % internalStorage.size
    }

    fun add(element: Short) {
        this + element
    }

    fun clear() {
        internalStorage.fill(0)
        writeIndex = 0
    }

    fun copyOf(): ShortArray {
        return internalStorage.copyOf()
    }

    operator fun get(index: Int): Short {
        require(index in internalStorage.indices) { "Index out of bounds: $index" }
        return internalStorage[(this.writeIndex + index) % internalStorage.size]
    }

    fun size(): Int = internalStorage.size
}