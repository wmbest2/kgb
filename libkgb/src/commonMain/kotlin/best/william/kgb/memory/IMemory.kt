package kgb.memory


interface IMemory {
    val addressRange: UIntRange
    operator fun set(position: UShort, value: UByte)
    operator fun get(position: UShort): UByte
}
