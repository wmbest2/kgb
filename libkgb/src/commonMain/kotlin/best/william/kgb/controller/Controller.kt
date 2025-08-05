package best.william.kgb.controller

import kgb.cpu.InterruptProvider
import kgb.util.bit

abstract class Controller(val interruptProvider: InterruptProvider) {

    // Represents the state of all of the buttons on the controller.
    // A, B, Select, Start, Up, Down, Left, Right.
    var buttonStates: UByte = 0u

    /**
     * Returns the current state of the controller.
     * The state is represented as a 16-bit unsigned integer, where each bit corresponds to a button.
     * The bits are defined as follows:

     * Selection Mode Direction Buttons:
     * - Bit 0: Right
     * - Bit 1: Left
     * - Bit 2: Up
     * - Bit 3: Down
     *
     * Selection Mode Action Buttons:
     * - Bit 0: A
     * - Bit 1: B
     * - Bit 2: Select
     * - Bit 3: Start
     */
    var JOYP: UByte = 0xCFu // Initial state of the joypad register (P1)
        get() {
            // The Game Boy joypad register works by selecting button groups
            // Bits 7-6: Not used (always 1)
            // Bit 5: Select action buttons (0 = select, 1 = not select)
            // Bit 4: Select direction buttons (0 = select, 1 = not select)
            // Bits 3-0: Button states (0 = pressed, 1 = released)

            var result = field and 0xF0u // Keep the selection bits

            when {
                field.bit(5) -> {
                    // Action buttons selected (A, B, Select, Start)
                    val actionButtons = (buttonStates.toUInt() shr 4).toUByte() and 0x0Fu
                    result = result or actionButtons.inv()
                }
                field.bit(4) -> {
                    // Direction buttons selected (Right, Left, Up, Down)
                    val directionButtons = buttonStates and 0x0Fu
                    result = result or directionButtons.inv()
                }
                else -> {
                    // No buttons selected
                    result = result or 0x0Fu
                }
            }

            return result
        }
        set(value) {
            field = (value and 0xF0u) or 0xCFu // Keep the upper nibble and set bits 7-6 to 1
        }

    fun handledInput() {
        // Default implementation returns false, can be overridden
        interruptProvider.requestInterrupt(4)
    }
}