package best.william.kgb.lcd

import best.william.kgb.controller.Controller
import kgb.cpu.InterruptProvider
import kgb.lcd.LCDRenderer
import kgb.util.bit
import kgb.util.withBit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import kotlin.coroutines.CoroutineContext

class LWJGLRenderer(interruptProvider: InterruptProvider) : LCDRenderer, Controller(interruptProvider) {
    private var window: Long = 0
    private val width = 160
    private val height = 144
    private val scale = 8 // Scale up for visibility

    // FPS tracking
    private var frameCount = 0
    private var lastFpsTime = System.currentTimeMillis()
    private var currentFps = 0.0

    private var screenBuffer = UByteArray(width * height)

    private var context: CoroutineContext = newSingleThreadContext("LWJGL renderer")
    private var coroutineScope = CoroutineScope(SupervisorJob() + context)


    init {
        coroutineScope.launch {
            if (!GLFW.glfwInit()) throw RuntimeException("Unable to initialize GLFW")
            window = GLFW.glfwCreateWindow(width * scale, height * scale, "Game Boy LCD", 0, 0)
            if (window == 0L) throw RuntimeException("Failed to create GLFW window")
            GLFW.glfwMakeContextCurrent(window)
            GL.createCapabilities()
            GL11.glClearColor(0f, 0f, 0f, 0f)
        }
    }

    // Map keys to Game Boy buttons
    // Action: A, B, Select, Start (bits 0-3 of buttonStates)
    // Direction: Right, Left, Up, Down (bits 4-7 of buttonStates)
    val keys = arrayOf(
        GLFW.GLFW_KEY_X, GLFW.GLFW_KEY_Z, GLFW.GLFW_KEY_O, GLFW.GLFW_KEY_P,
        GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_LEFT, GLFW.GLFW_KEY_UP, GLFW.GLFW_KEY_DOWN
    )

    fun checkInput() {
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            GLFW.glfwSetWindowShouldClose(window, true)
        }

        val inputChanged = keys.withIndex()
            .map { (i, key) ->
                val output = checkKey(key, i)
                output
            }
            .any { it }

        if (inputChanged) {
            // Only trigger joypad interrupt if system was in a low-power state
            // For most normal gameplay, joypad changes don't trigger interrupts
            handledInput()
        }
    }

    private fun checkKey(
        key: Int,
        i: Int,
    ): Boolean {
        val isPressed = GLFW.glfwGetKey(window, key) == GLFW.GLFW_PRESS
        val wasPressed = buttonStates.bit(i)

        buttonStates = buttonStates.withBit(i, isPressed)

        if (isPressed != wasPressed) {
            return true
        }
        return false
    }

    override fun render(pixels: UByteArray) {
        coroutineScope.launch {
            pixels.copyInto(screenBuffer)

            GLFW.glfwPollEvents()

            checkInput()
            // Update FPS calculation
            frameCount++
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastFpsTime >= 1000) {
                currentFps = frameCount.toDouble() / ((currentTime - lastFpsTime) / 1000.0)
                // Update window title with FPS
                GLFW.glfwSetWindowTitle(window, "Game Boy LCD - FPS: ${String.format("%.1f", currentFps)}")
                lastFpsTime = currentTime
                frameCount = 0
            }

            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)

            // Fixed rendering - removed duplicate glBegin calls
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val idx = y * width + x
                    val shade = screenBuffer[idx].toInt() and 0xFF
                    val color = when (shade) {
                        0 -> floatArrayOf(1f, 1f, 1f) // White
                        1 -> floatArrayOf(0.7f, 0.7f, 0.7f) // Light gray
                        2 -> floatArrayOf(0.4f, 0.4f, 0.4f) // Dark gray
                        else -> floatArrayOf(0f, 0f, 0f) // Black
                    }
                    GL11.glColor3f(color[0], color[1], color[2])
                    val x0 = (x.toFloat() / width) * 2f - 1f
                    val y0 = ((height - 1 - y).toFloat() / height) * 2f - 1f
                    val x1 = ((x + 1).toFloat() / width) * 2f - 1f
                    val y1 = ((height - y).toFloat() / height) * 2f - 1f
                    GL11.glBegin(GL11.GL_QUADS)
                    GL11.glVertex2f(x0, y0)
                    GL11.glVertex2f(x1, y0)
                    GL11.glVertex2f(x1, y1)
                    GL11.glVertex2f(x0, y1)
                    GL11.glEnd()
                }
            }
            GLFW.glfwSwapBuffers(window)
        }
    }

    fun shouldClose(): Boolean {
        var shouldClose = false
        coroutineScope.launch {
             shouldClose = GLFW.glfwWindowShouldClose(window)
        }
        return shouldClose
    }

    fun dispose() {
        coroutineScope.launch {
            GLFW.glfwDestroyWindow(window)
            GLFW.glfwTerminate()
        }
    }
}
