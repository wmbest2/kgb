@file:Suppress("FunctionName")

package best.william.kgb.cpu
import kgb.memory.IMemory
import kgb.cpu.InterruptProvider
import kgb.util.*
import kotlinx.coroutines.newSingleThreadContext
import kotlin.time.measureTime

@ExperimentalUnsignedTypes
class LR35902(
    val memory: IMemory,
    registers: IRegisters = Registers()
): IRegisters by registers, InterruptProvider {

    val scheduler = newSingleThreadContext("MyOwnThread")

    // region Status Bits
    var zeroBit by flag(::F, 7)
    var subtractBit by flag(::F, 6)
    var halfCarryBit by flag(::F, 5)
    var carryBit by flag(::F, 4)
    // endregion

    // region Registers
    private var stackPointer: UShort = 0xFFFEu // Defaults to last ram point
    var programCounter: UShort = 0x0100u
    // endregion

    // region Operation flags
    private var HALT = false
    private var STOPPED = false
    private var interruptsEnabled = true
    // endregion

    fun step(debug: Boolean = false) {
        if (debug) {
            println(buildString {
                append("PC: 0x${programCounter.toString(16)}")
                var opcode: UInt
                val time = measureTime {
                    opcode = performStep()
                }
                append(" OPCODE: 0x${opcode.toString(16)}")
                append("  ${opcode.toAssemblyString()}")
                append(" TIME: $time")
            })
        } else {
            performStep()
        }
    }

    private fun performStep(): UInt {
        if (HALT || STOPPED) {
            // Skip only once if interrupts are disabled [ See gbcpuman.pdf, Page 19 ]
            if (!interruptsEnabled) HALT = false
            return 0u
        }
        val opcode = memory[programCounter++].toUInt()

        when(opcode) {
            // Commands
            0x00u -> {}  // NoOp
            0x10u -> STOPPED = true
            0x76u -> HALT = true
            0xF3u -> DI()
            0xFBu -> EI()

            // Flag Operations
            0x37u -> SCF()
            0x3Fu -> CCF()

            // Jump
            0x20u, 0x30u, 0x18u,
            0x28u, 0x38u -> `JR n r8`(opcode)
            0xC3u, 0xC2u, 0xD2u,
            0xCAu, 0xDAu-> `JP n a16`(opcode)
            0xE9u -> `JP _HL`()

            // Load Immediate 8bit value
            0x06u, 0x0Eu, 0x16u,
            0x1Eu, 0x26u, 0x2Eu,
            0x36u, 0x3Eu -> `LD n d8`(opcode)

            //Load Registers
            0xEAu -> `LD _a16 A`()
            0xFAu -> `LD A _a16`()
            0xE2u -> `LD _C A`()
            0xF2u -> `LD A _C`()
            0xE0u -> `LDH _a8 A`()
            0xF0u -> `LDH A _a8`()
            0x02u, 0x12u, 0x22u, 0x32u -> `LD _nn A`(opcode)
            0x0Au, 0x1Au, 0x2Au, 0x3Au -> `LD A _nn`(opcode)

            0x01u, 0x11u,
            0x21u, 0x31u -> `LD nn d16`(opcode)
            in 0x40u..0x7Fu -> `LD r1 r2`(opcode)

            // Math Operations
            0x07u -> RLCA()
            0x17u -> RLA()
            0x0Fu -> RRCA()
            0x1Fu -> RRA()
            0x2Fu -> CPL()
            0x03u, 0x13u, 0x23u, 0x33u -> `INC nn`(opcode)
            0x04u, 0x14u, 0x24u, 0x34u,
            0x0Cu, 0x1Cu, 0x2Cu, 0x3Cu -> `INC n`(opcode)
            0x0Bu, 0x1Bu, 0x2Bu, 0x3Bu -> `DEC nn`(opcode)
            0x05u, 0x15u, 0x25u, 0x35u,
            0x0Du, 0x1Du, 0x2Du, 0x3Du -> `DEC n`(opcode)
            in 0x80u..0x87u -> `ADD A r`(opcode)
            in 0x88u..0x8Fu -> `ADC A r`(opcode)
            0x19u -> `ADD HL DE`()
            0xC6u -> `ADD A d8`()
            in 0x90u..0x97u -> `SUB A r`(opcode)
            in 0x98u..0x9Fu -> `SBC A r`(opcode)
            in 0xA0u..0xA7u -> `AND r`(opcode)
            in 0xA8u..0xAFu -> `XOR r`(opcode)
            in 0xB0u..0xB7u -> `OR r`(opcode)
            in 0xB8u..0xBFu -> `CP r`(opcode)
            0xE6u -> `AND d8`()
            0xF6u -> `OR d8`()
            0xFEu -> `CP d8`()

            // Stack Operations
            0xC9u -> RET()
            0xC0u, 0xD0u, 0xC8u, 0xD8u -> `RET cc`(opcode)
            0xC1u, 0xD1u, 0xE1u, 0xF1u -> `POP nn`(opcode)
            0xC5u, 0xD5u, 0xE5u, 0xF5u -> `PUSH nn`(opcode)
            0xC4u, 0xD4u,
            0xCCu, 0xCDu, 0xDCu -> `CALL n a16`(opcode)
            0xC7u, 0xD7u, 0xE7u, 0xF7u,
            0xCFu, 0xDFu, 0xEFu, 0xFFu -> `RST nn`(opcode)


            // Bit Operations
            0xCBu -> executeCB()
            else -> TODO("Implement Opcode '${opcode.toString(16)}' ${opcode.toUByte().toAssemblyString()}\n${this}")
        }

        return opcode
    }

    private fun executeCB() {
        when (val cbOp = memory[programCounter++].toUInt()) {
            in 0x00u..0x07u -> `RLC n`(cbOp)
            in 0x08u..0x0Fu -> `RRC n`(cbOp)
            in 0x10u..0x17u -> `RL n`(cbOp)
            in 0x18u..0x1Fu -> `RR n`(cbOp)
//                in 0x20u..0x27u -> `SLA n`(cbOp)
//                in 0x28u..0x2Fu -> `SRA n`(cbOp)
            in 0x30u..0x37u -> `SWAP n`(cbOp)
//                in 0x38u..0x3Fu -> `SRL n`(cbOp)
            in 0x40u..0x7Fu -> `BIT b n`(cbOp)
            in 0x80u..0xBFu -> `RES b n`(cbOp)
            in 0xC0u..0xFFu -> `SET b n`(cbOp)
            else -> TODO("Implement Opcode 'CB ${cbOp.toString(16)}'\n${this}")
        }
    }

    //region Opcodes

    //region Commands
    private fun DI() {
        interruptsEnabled = false
    }

    private fun EI() {
        interruptsEnabled = true
    }

    private fun SCF() {
        carryBit = true
        halfCarryBit = false
        subtractBit = false
    }

    private fun CCF() {
        carryBit = !carryBit
        halfCarryBit = false
        subtractBit = false
    }
    //endregion

    // region Jump Ops

    private fun `JR n r8`(opcode: UInt) {
        val condition = when(opcode) {
            0x18u -> true // JR r8
            0x20u -> !zeroBit
            0x30u -> !carryBit
            0x28u -> zeroBit
            0x38u -> carryBit
            else -> false
        }

        val relativeAddress = memory[programCounter++]
        if (condition) {
            programCounter = (programCounter.toShort() + relativeAddress.toByte()).toUShort()
        }
    }


    private fun `JP n a16`(opcode: UInt) {
        val condition = when(opcode) {
            0xC3u -> true // JP a16
            0xC2u -> !zeroBit
            0xD2u -> !carryBit
            0xCAu -> zeroBit
            0xDAu -> carryBit
            else -> false
        }

        val address = asWord(memory[programCounter++], memory[programCounter++])
        if (condition) {
            programCounter = address
        }
    }

    private fun `JP _HL`() {
        programCounter = HL
    }

    private fun compare(value: UByte) {
        zeroBit = A == value
        carryBit = A < value
        halfCarryBit = (A and 0xFu) < (value and 0xFu)
        subtractBit = true
    }

    private fun `CP r`(opcode: UInt) {
        compare(readRegisterBy(opcode % 8u))
    }

    private fun `CP d8`() {
        compare(memory[programCounter++])
    }
    // endregion

    // region Load Ops

    private fun `LD n d8`(opcode: UInt) {
        val value = memory[programCounter++]
        when (opcode) {
            0x3Eu -> A = value
            0x06u -> B = value
            0x0eu -> C = value
            0x16u -> D = value
            0x1eu -> E = value
            0x26u -> H = value
            0x2eu -> L = value
            0x36u -> memory[HL] = value
        }
    }

    private fun `LD _nn A`(opcode: UInt) {
        when(opcode) {
            0x02u -> memory[BC] = A
            0x12u -> memory[DE] = A
            0x22u -> memory[HL++] = A
            0x32u -> memory[HL--] = A
        }
    }

    private fun `LD A _nn`(opcode: UInt) {
        when(opcode) {
            0x0Au -> A = memory[BC]
            0x1Au -> A = memory[DE]
            0x2Au -> A = memory[HL++]
            0x3Au -> A = memory[HL--]
        }
    }

    private fun `LD nn d16`(opcode: UInt) {
        val value = asWord(memory[programCounter++], memory[programCounter++])
        when(opcode) {
            0x01u -> BC = value
            0x11u -> DE = value
            0x21u -> HL = value
            0x31u -> stackPointer = value
        }
    }

    private fun `LD _a16 A`() {
        val low = memory[programCounter++]
        val high = memory[programCounter++]
        memory[asWord(low, high)] = A
    }

    private fun `LD A _a16`() {
        val low = memory[programCounter++]
        val high = memory[programCounter++]
        A = memory[asWord(low, high)]
    }

    private fun `LD _C A`() {
        val address = (0xFF00u + C).toUShort()
        memory[address] = A
    }

    private fun `LD A _C`() {
        val address = (0xFF00u + C).toUShort()
        A = memory[address]
    }

    private val ldhTop: UShort = 0xFF00u

    private fun `LDH _a8 A`() {
        val address = ldhTop or memory[programCounter++].toUShort()
        memory[address] = A
    }

    private fun `LDH A _a8`() {
        val address = ldhTop or memory[programCounter++].toUShort()
        A = memory[address]
    }

    private fun `LD r1 r2`(opcode: UInt) {
        val writeOffset = (opcode - 0x40u) / 8u
        val readOffset = opcode % 8u
        writeRegisterBy(writeOffset, readRegisterBy(readOffset))
    }
    // endregion

    // region Math Ops

    private fun `INC nn`(opcode: UInt) {
        when (opcode) {
            0x03u -> BC++
            0x13u -> DE++
            0x23u -> HL++
            0x33u -> stackPointer++
        }
    }

    private fun `INC n`(opcode: UInt) {

        val result = when (opcode) {
            0x04u -> ++B
            0x0Cu -> ++C
            0x14u -> ++D
            0x1Cu -> ++E
            0x24u -> ++H
            0x2Cu -> ++L
            0x34u -> ++memory[HL]
            0x3Cu -> ++A
            else -> TODO()
        }

        val original = (result - 1u).toUByte()

        zeroBit = result == UByte.ZERO
        halfCarryBit = original.bit(3) && !result.bit(3)
        subtractBit = false
    }

    private fun `DEC nn`(opcode: UInt) {
        when (opcode) {
            0x0Bu -> BC--
            0x1Bu -> DE--
            0x2Bu -> HL--
            0x3Bu -> stackPointer--
        }
    }

    private fun `DEC n`(opcode: UInt) {
        val result = when (opcode) {
            0x05u -> --B
            0x0Du -> --C
            0x15u -> --D
            0x1Du -> --E
            0x25u -> --H
            0x2Du -> --L
            0x35u -> --memory[HL]
            0x3Du -> --A
            else -> TODO()
        }
        val original = (result + 1u).toUByte()

        zeroBit = result == UByte.ZERO
        halfCarryBit = original.bit(4) && !result.bit(4)
        subtractBit = true
    }

    private fun `ADD A r`(opcode: UInt) {
        val offset = opcode % 8u
        val original = A
        val operand = readRegisterBy(offset)
        val result = original + operand
        A = result.toUByte()
        zeroBit = A == UByte.ZERO
        carryBit = result.bit(8)
        halfCarryBit =  (original and 0xFu.toUByte()) + (operand and 0xFu.toUByte()) > 0xFu
        subtractBit = false
    }

    private fun `ADC A r`(opcode: UInt) {
        val offset = opcode % 8u
        val carry = if (carryBit) 1u else 0u
        val original = A
        val operand = readRegisterBy(offset)
        val result = original + operand + carry
        A = result.toUByte()
        zeroBit = A == UByte.ZERO
        carryBit = result.bit(8)
        halfCarryBit =  (original and 0xFu.toUByte()) + (operand and 0xFu.toUByte()) + carry > 0xFu
        subtractBit = false
    }

    private fun `ADD HL DE`() {
        val original = HL
        val operand = DE
        val result = original + operand
        HL = result.toUShort()
        zeroBit = false // HL is never zero
        carryBit = result.bit(16)
        halfCarryBit = (original and 0x0FFFu) + (operand and 0x0FFFu) > 0x0FFFu
        subtractBit = false
    }

    private fun `ADD A d8`() {
        val operand = memory[programCounter++]
        val original = A
        val result = original + operand
        A = result.toUByte()
        zeroBit = A == UByte.ZERO
        carryBit = result.bit(8)
        halfCarryBit = (original and 0xFu.toUByte()) + (operand and 0xFu.toUByte()) > 0xFu
        subtractBit = false
    }

    private fun `SUB A r`(opcode: UInt) {
        val offset = opcode % 8u
        val operand = readRegisterBy(offset)
        val original = A
        val result = original - operand
        A = result.toUByte()
        zeroBit = A == UByte.ZERO
        carryBit = original < operand
        halfCarryBit = ((original.toUInt() xor operand.toUInt() xor result) and 0x10u) != 0u
        subtractBit = true
    }

    private fun `SBC A r`(opcode: UInt) {
        val offset = opcode % 8u
        val operand = readRegisterBy(offset)
        val carry = if (carryBit) 1u else 0u
        val original = A
        val result = original - operand - carry
        A = result.toUByte()
        zeroBit = A == UByte.ZERO
        carryBit = original < (operand + carry).toUByte()
        halfCarryBit = ((original.toUInt() xor operand.toUInt() xor result) and 0x10u) != 0u
        subtractBit = true
    }

    private fun CPL() {
        subtractBit = true
        halfCarryBit = true
        A = A.inv()
    }

    private fun `AND r`(opcode: UInt) {
        val offset = opcode % 8u
        A = readRegisterBy(offset) and A

        zeroBit = A == UByte.ZERO
        carryBit = false
        halfCarryBit = true
        subtractBit = false
    }

    private fun `AND d8`() {
        A = memory[programCounter++] and A

        zeroBit = A == UByte.ZERO
        carryBit = false
        halfCarryBit = true
        subtractBit = false
    }

    private fun `XOR r`(opcode: UInt) {
        val offset = opcode % 8u
        val value = readRegisterBy(offset) xor A
        A = value

        zeroBit = value == UByte.ZERO
        carryBit = false
        halfCarryBit = false
        subtractBit = false
    }

    private fun `OR r`(opcode: UInt) {
        val offset = opcode % 8u
        A = readRegisterBy(offset) or A

        zeroBit = A == UByte.ZERO
        carryBit = false
        halfCarryBit = false
        subtractBit = false
    }


    private fun `OR d8`() {
        A = memory[programCounter++] or A

        zeroBit = A == UByte.ZERO
        carryBit = false
        halfCarryBit = false
        subtractBit = false
    }

    // endregion

    // region Bit Ops

    // These commands are similar to the bit commands so we can just overload them
    private fun RLA() = `RL n`(0x17u, false)
    private fun RLCA() = `RL n`(0x07u, false)
    private fun RRA() = `RL n`(0x1Fu, false)
    private fun RRCA() = `RL n`(0x0Fu, false)

    private fun `RL n`(opcode: UInt, setZero: Boolean = true) {
        val offset = opcode % 8u

        val value = readRegisterBy(offset).toUInt() shl 1
        val output = value.withBit(0, value.bit(8)).toUByte() // Shift left setting the 0 bit to the old 7 bit value
        writeRegisterBy(offset, output)
        carryBit = value.bit(8)
        if (setZero)
            zeroBit = output == UByte.ZERO
    }

    private fun `RLC n`(opcode: UInt, setZero: Boolean = true) {
        val offset = opcode % 8u

        val value = readRegisterBy(offset).toUInt() shl 1
        val output = value.withBit(0, carryBit).toUByte() // Shift left setting the 0 bit to the carryBit
        writeRegisterBy(offset, output)
        carryBit = value.bit(8)
        if (setZero)
            zeroBit = output == UByte.ZERO
    }

    private fun `RR n`(opcode: UInt, setZero: Boolean = true) {
        val offset = opcode % 8u

        val registerData = readRegisterBy(offset)
        val oldZero = registerData.bit(0)
        val value = registerData.toUInt() shr 1

        val output = value.withBit(7, oldZero).toUByte() // Shift left setting the 0 bit to the old 7 bit value
        writeRegisterBy(offset, output)
        carryBit = oldZero
        if (setZero)
            zeroBit = output == UByte.ZERO
    }

    private fun `RRC n`(opcode: UInt, setZero: Boolean = true) {
        val offset = opcode % 8u

        val registerData = readRegisterBy(offset)
        val oldZero = registerData.bit(0)
        val value = registerData.toUInt() shr 1

        val output = value.withBit(7, carryBit).toUByte() // Shift left setting the 0 bit to the carryBit
        writeRegisterBy(offset, output)
        carryBit = oldZero
        if (setZero)
            zeroBit = output == UByte.ZERO
    }

    private fun `SWAP n`(opcode: UInt) {
        val offset = opcode % 8u

        val value = readRegisterBy(offset).swapNibbles()
        writeRegisterBy(offset, value)
        carryBit = false
        halfCarryBit = false
        zeroBit = value == UByte.ZERO
        subtractBit = false

    }

    private fun `BIT b n`(opcode: UInt) {
        val offset = opcode % 8u
        val bit = (opcode - 0x40u) / 8u

        halfCarryBit = true
        carryBit = false
        zeroBit = !readRegisterBy(offset).bit(bit.toInt())
    }

    private fun `RES b n`(opcode: UInt) {
        val offset = opcode % 8u
        val bit = (opcode - 0x80u) / 8u
        setBitForRegisterTo(offset, bit.toInt(), false)
    }

    private fun `SET b n`(opcode: UInt) {
        val offset = opcode % 8u
        val bit = (opcode - 0xC0u) / 8u
        setBitForRegisterTo(offset, bit.toInt(), true)
    }
    //endregion

    //region Stack Operations

    private fun `RST nn`(opcode: UInt) {
        val address: UShort = when(opcode) {
            0xC7u -> 0x00u
            0xD7u -> 0x10u
            0xE7u -> 0x20u
            0xF7u -> 0x30u
            0xCFu -> 0x08u
            0xDFu -> 0x18u
            0xEFu -> 0x28u
            0xFFu -> 0x38u
            else -> TODO("Should be exhaustive")
        }

        memory[stackPointer--] = programCounter.highByte
        memory[stackPointer--] = programCounter.lowByte

        programCounter = address
    }

    private fun `CALL n a16`(opcode: UInt) {
        val condition = when(opcode) {
            0xCDu -> true
            0xC4u -> !zeroBit
            0xD4u -> !carryBit
            0xCCu -> zeroBit
            0xDCu -> carryBit
            else -> false
        }

        val jumpAddress = asWord(memory[programCounter++], memory[programCounter++])

        if (condition) {
            memory[stackPointer--] = programCounter.highByte
            memory[stackPointer--] = programCounter.lowByte

            programCounter = jumpAddress
        }
    }

    private fun `PUSH nn`(opcode: UInt) {

        val value = when (opcode) {
            0xC5u -> BC
            0xD5u -> DE
            0xE5u -> HL
            0xF5u -> AF
            else -> TODO()
        }

        memory[stackPointer--] = value.highByte
        memory[stackPointer--] = value.lowByte
    }

    private fun `POP nn`(opcode: UInt) {
        val value = asWord(memory[++stackPointer], memory[++stackPointer])

        when (opcode) {
            0xC1u -> BC = value
            0xD1u -> DE = value
            0xE1u -> HL = value
            0xF1u -> AF = value
            else -> TODO()
        }

    }

    private fun RET() {
        programCounter = asWord(memory[++stackPointer], memory[++stackPointer])
    }

    private fun `RET cc`(opcode: UInt) {
        val condition = when(opcode) {
            0xC0u -> !zeroBit
            0xD0u -> !carryBit
            0xC8u -> zeroBit
            0xD8u -> carryBit
            else -> false
        }
        if (condition)
            RET()
    }

    //endregion

    // region Helpers
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

    private fun setBitForRegisterTo(offset: UInt, bit: Int, value: Boolean) {
        writeRegisterBy(offset, readRegisterBy(offset).withBit(bit, value))
    }

    //endregion

    override fun toString(): String {
        return """
            registers [
                A: 0x${A.toString(16)},
                F: 0x${F.toString(16)},
                B: 0x${B.toString(16)},
                C: 0x${C.toString(16)},
                D: 0x${D.toString(16)},
                E: 0x${E.toString(16)},
                H: 0x${H.toString(16)},
                L: 0x${L.toString(16)}
            ]
            PC: 0x${programCounter.toString(16)}
            SP: 0x${stackPointer.toString(16)}
        """.trimIndent()
    }

    override fun requestInterrupt(interruptID: Int) {

    }

}
