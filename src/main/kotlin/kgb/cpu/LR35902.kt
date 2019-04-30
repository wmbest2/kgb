package best.william.kgb.cpu
import best.william.kgb.memory.IMemory
import kotlin.contracts.contract

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


    fun reset() {
        stackPointer = 0xFFFEu
        programCounter = 0x0100u

        cpuHalted = false
        interruptsEnabled = true
    }

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
            0x76u -> halt()
            0xF3u -> di()
            0xFBu -> ei()

            // Flag Operations
            0x37u -> scf()
            0x3Fu -> ccf()

            // Jump
            0xC3u -> jp()

            // Load Immediate 8bit value
            0x06u, 0x0Eu, 0x16u,
            0x1Eu, 0x26u, 0x2Eu, 0x3Eu -> `LD n, d8`(opcode)

            //Load Registers
            in 0x78u..0x7Fu -> `LD A, n`(opcode)
            0xFAu -> `LD A, (a16)`()

            in 0x40u..0x47u -> `LD B, n`(opcode)
            in 0x48u..0x4Fu -> `LD C, n`(opcode)
            in 0x50u..0x57u -> `LD D, n`(opcode)
            in 0x58u..0x5Fu -> `LD E, n`(opcode)
            in 0x60u..0x67u -> `LD H, n`(opcode)
            in 0x68u..0x6Fu -> `LD L, n`(opcode)

            else -> throw NotImplementedError()
        }
    }

    private fun halt() {
        cpuHalted = true
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

    private fun jp() {
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

    private fun readFromRegister(offset: UInt): UByte {
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

    private fun `LD A, n`(opcode: UInt) {
        A = readFromRegister(opcode - 0x78u)
    }

    private fun `LD A, (a16)`() {
        val low = memory[programCounter++]
        val high = memory[programCounter++]
        A = memory[asWord(low, high)]
    }

    private fun `LD B, n`(opcode: UInt) {
        B = readFromRegister(opcode - 0x40u)
    }

    private fun `LD C, n`(opcode: UInt) {
        C = readFromRegister(opcode - 0x48u)
    }

    private fun `LD D, n`(opcode: UInt) {
        D = readFromRegister(opcode - 0x50u)
    }

    private fun `LD E, n`(opcode: UInt) {
        E = readFromRegister(opcode - 0x58u)
    }

    private fun `LD H, n`(opcode: UInt) {
        H = readFromRegister(opcode - 0x60u)
    }

    private fun `LD L, n`(opcode: UInt) {
        L = readFromRegister(opcode - 0x68u)
    }
}
