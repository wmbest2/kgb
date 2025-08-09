package best.william.kgb.util

fun Int.scale4BitTo(range: IntRange): Short {
    // Clamp value to 0..15
    val clamped = coerceIn(0, 15)
    // Scale to -32768..32767
    return ((clamped / 15.0) * range.last * 2 - range.last).toInt().coerceIn(range.first, range.last).toShort()
}