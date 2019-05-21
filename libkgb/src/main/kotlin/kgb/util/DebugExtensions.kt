package kgb.util

import best.william.kgb.cpu.LR35902

@ExperimentalUnsignedTypes
fun LR35902.debugCurrentOperation() {
    val command = memory[programCounter]
    val (code, assembly) = if (command != 0xCBu.toUByte()) {
        command.toString(16).padStart(2, '0') to command.toAssemblyString()
    } else {
        val cbCommand = memory[(programCounter+1u).toUShort()]
        "CB ${cbCommand.toString(16).padStart(2, '0')}" to cbCommand.toCBAssemblyString()
    }
    println("${programCounter.toString(16)}: $code $assembly")
}

@ExperimentalUnsignedTypes
fun UByte.toAssemblyString(): String {
    return when(this.toUInt()) {
        // Commands
        0x00u -> "NOOP"  // NoOp
//            0x10u -> STOP
        0x76u -> "HALT"
        0xF3u -> "DI"
        0xFBu -> "EI"

        // Flag Operations
        0x37u -> "SCF"
        0x3Fu -> "CCF"

        // Jump
        0x20u, 0x30u, 0x18u,
        0x28u, 0x38u -> "JR n, r8"
        0xC3u, 0xC2u, 0xD2u,
        0xCAu, 0xDAu-> "JP n, a16"

        // Load Immediate 8bit value
        0x06u, 0x0Eu, 0x16u,
        0x1Eu, 0x26u, 0x2Eu, 0x3Eu -> "LD n, d8"

        //Load Registers
        0xEAu -> "LD (a16), A"
        0xFAu -> "LD A, (a16)"
        0xE2u -> "LD (C), A"
        0xF2u -> "LD A, (C)"
        0xE0u -> "LDH (a8), A"
        0xF0u -> "LDH A, (a8)"
        0x02u, 0x12u, 0x22u, 0x32u -> "LD (nn), A"
        0x0Au, 0x1Au, 0x2Au, 0x3Au -> "LD A, (nn)"

        0x01u, 0x11u,
        0x21u, 0x31u -> "LD nn, d16"
        in 0x40u..0x7Fu -> "LD r1, r2"

        // Math Operations
        0x07u -> "RLCA"
        0x17u -> "RLA"
        0x0Fu -> "RRCA"
        0x1Fu -> "RRA"
        0x03u, 0x13u, 0x23u, 0x33u -> "INC nn"
        0x04u, 0x14u, 0x24u, 0x34u,
        0x0Cu, 0x1Cu, 0x2Cu, 0x3Cu -> "INC n"
        0x0Bu, 0x1Bu, 0x2Bu, 0x3Bu -> "DEC nn"
        0x05u, 0x15u, 0x25u, 0x35u,
        0x0Du, 0x1Du, 0x2Du, 0x3Du -> "DEC n"
        in 0x80u..0x87u -> "ADD A, r"
        in 0x88u..0x8Fu -> "ADC A, r"
        in 0x90u..0x97u -> "SUB A, r"
        in 0x98u..0x97u -> "SBC A, r"
        in 0xA0u..0xA7u -> "AND r"
        in 0xA8u..0xAFu -> "XOR r"
        in 0xB0u..0xB7u -> "OR r"
        in 0xB8u..0xBFu -> "CP r"
        0xFEu -> "CP d8"

        // Stack Operations
        0xC0u, 0xD0u, 0xC8u, 0xD8u -> "RET n"
        0xC1u, 0xD1u, 0xE1u, 0xF1u -> "POP nn"
        0xC5u, 0xD5u, 0xE5u, 0xF5u -> "PUSH nn"
        0xC4u, 0xD4u,
        0xCCu, 0xCDu, 0xDCu -> "CALL n, a16"


        // Bit Operations
        0xCBu -> "See CB Table"

        else -> TODO("Implement Opcode '${this.toString(16)}'\n${this}")
    }
}

@ExperimentalUnsignedTypes
fun UByte.toCBAssemblyString(): String {
    return when (this.toUInt()) {
        in 0x00u..0x07u -> "RLC n"
        in 0x08u..0x0Fu -> "RRC n"
        in 0x10u..0x17u -> "RL n"
        in 0x18u..0x1Fu -> "RR n"
        in 0x20u..0x27u -> "SLA n"
        in 0x28u..0x2Fu -> "SRA n"
        in 0x30u..0x37u -> "SWAP n"
        in 0x38u..0x3Fu -> "SRL n"
        in 0x40u..0x7Fu -> "BIT b, n"
        in 0x80u..0xBFu -> "RES b, n"
        in 0xC0u..0xFFu -> "SET b, n"
        else -> TODO("Implement Opcode 'CB ${this.toString(16)}'\n${this}")
    }
}