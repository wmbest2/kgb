package best.william.kgb.cpu
import best.william.kgb.memory.IMemory
import kgb.util.bit
import kgb.util.withBit

@ExperimentalUnsignedTypes
class LR35902(
        private val memory: IMemory,
        registers: IRegisters = Registers()
): IRegisters by registers {

    var zeroBit by flag(7)
    var subtractBit by flag(6)
    var halfCarryBit by flag(5)
    var carryBit by flag(4)

    var stackPointer: UShort = 0xFFFEu // Defaults to last ram point
    var programCounter: UShort = 0x0100u

    private var cpuHalted = false
    private var interruptsEnabled = true


    fun step() {
        if (cpuHalted) {
            // Skip only once if interrupts are disabled [ See gbcpuman.pdf, Page 19 ]
            if (!interruptsEnabled) cpuHalted = false
            return
        }

        val command = memory[programCounter++]
        when(val opcode = command.toUInt()) {
            // Commands
            0x00u -> {}  // NoOp
            0x76u -> cpuHalted = true // HALT
            0xF3u -> di()
            0xFBu -> ei()

            // Flag Operations
            0x37u -> scf()
            0x3Fu -> ccf()

            // Jump
            0xC3u -> `JP (a16)`()

            // Load Immediate 8bit value
            0x06u, 0x0Eu, 0x16u,
            0x1Eu, 0x26u, 0x2Eu, 0x3Eu -> `LD n, d8`(opcode)

            //Load Registers
            0xFAu -> `LD A, (a16)`()

//            in 0x80u..0x87u -> `ADD A, r`()
//            in 0x88u..0x8Fu -> `ADC A, r`()
//            in 0x90u..0x97u -> `SUB A, r`()
//            in 0x98u..0x97u -> `SBC A, r`()
//            in 0xA0u..0xA7u -> `AND r`()
//            in 0xA8u..0xAFu -> `XOR r`()
            in 0xB0u..0xB7u -> `OR r`(opcode)
//            in 0xB8u..0xBFu -> `CP r`()
            in 0x40u..0x7Fu -> `LD r1, r2`(opcode)


            0xCBu -> when (val cbOp = memory[programCounter++].toUInt()) {
                in 0x40u..0x7Fu -> `BIT b, n`(cbOp)
                in 0x80u..0xBFu -> `RES b, n`(cbOp)
                in 0xC0u..0xFFu -> `SET b, n`(cbOp)
                else -> throw NotImplementedError()
            }

            else -> throw NotImplementedError()
        }
    }

    private fun di() {
        interruptsEnabled = false
    }

    private fun ei() {
        interruptsEnabled = true
    }

    private fun scf() {
        carryBit = true
        halfCarryBit = false
        subtractBit = false
    }

    private fun ccf() {
        halfCarryBit = false
        carryBit = !carryBit
        subtractBit = false
    }

    private fun `JP (a16)`() {
        programCounter = asWord(memory[programCounter++], memory[programCounter++])
    }

    private fun `LD n, d8`(opcode: UInt) {
        val value = memory[programCounter++]
        when (opcode) {
            0x3Eu -> A = value
            0x06u -> B = value
            0x0eu -> C = value
            0x16u -> D = value
            0x1eu -> E = value
            0x26u -> H = value
            0x2eu -> L = value
        }
    }

    /**
     * Many commands use the same offset scheme to determine
     * which register to write.
     *
     * This simplifies read commands to a single call
     */
    private fun writeRegisterBy(offset: UInt, value: UByte) {
        when (offset) {
            0x7u -> A = value
            0x0u -> B = value
            0x1u -> C = value
            0x2u -> D = value
            0x3u -> E = value
            0x4u -> H = value
            0x5u -> L = value
            0x6u -> memory[HL] = value
        }
    }

    /**
     * Many commands use the same offset scheme to determine
     * which register to read.
     *
     * This simplifies read commands to a single call
     */
    private fun readRegisterBy(offset: UInt): UByte {
        return when (offset) {
            0x7u -> A
            0x0u -> B
            0x1u -> C
            0x2u -> D
            0x3u -> E
            0x4u -> H
            0x5u -> L
            0x6u -> memory[HL]
            else -> throw NotImplementedError("$offset not supported")
        }
    }

    private fun `LD A, (a16)`() {
        val low = memory[programCounter++]
        val high = memory[programCounter++]
        A = memory[asWord(low, high)]
    }

    private fun `LD r1, r2`(opcode: UInt) {
        val writeOffset = (opcode - 0x40u) / 8u
        val readOffset = opcode % 8u
        writeRegisterBy(writeOffset, readRegisterBy(readOffset))
    }

    private fun `OR r`(opcode: UInt) {
        val offset = opcode % 8u
        A = readRegisterBy(offset) or A

        zeroBit = A == 0x0u.toUByte()
        carryBit = false
        halfCarryBit = false
        subtractBit = false
    }

    private fun setBitForRegisterTo(offset: UInt, bit: Int, value: Boolean) {
        writeRegisterBy(offset, readRegisterBy(offset).withBit(bit, value))
    }

    private fun `BIT b, n`(opcode: UInt) {
        val offset = opcode % 8u
        val bit = (opcode - 0x40u) / 8u

        halfCarryBit = true
        carryBit = false
        zeroBit = !readRegisterBy(offset).bit(bit.toInt())
    }

    private fun `RES b, n`(opcode: UInt) {
        val offset = opcode % 8u
        val bit = (opcode - 0x80u) / 8u
        setBitForRegisterTo(offset, bit.toInt(), false)
    }

    private fun `SET b, n`(opcode: UInt) {
        val offset = opcode % 8u
        val bit = (opcode - 0xC0u) / 8u
        setBitForRegisterTo(offset, bit.toInt(), true)
    }
}
