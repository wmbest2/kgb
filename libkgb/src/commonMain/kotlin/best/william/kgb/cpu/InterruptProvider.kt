package kgb.cpu

interface InterruptProvider {
    fun requestInterrupt(interruptID: Int)
}