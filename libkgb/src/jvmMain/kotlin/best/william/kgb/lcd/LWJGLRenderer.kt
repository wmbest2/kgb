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

class LWJGLRenderer() : LCDRenderer, Controller() {
    private var window: Long = 0
    private val width = 160
    private val height = 144
    private val scale = 8 // Scale up for visibility

    // FPS tracking
    private var frameCount = 0
    private var lastFpsTime = System.currentTimeMillis()
    private var currentFps = 0.0

    private var dirty = false // Track if the frame buffer has changed

    var frameBuffer = UByteArray(width * height) // Frame buffer for rendering

    private var textureId: Int = 0
    private val pixelBuffer = java.nio.ByteBuffer.allocateDirect(width * height * 3)

    init {
        if (!GLFW.glfwInit()) throw RuntimeException("Unable to initialize GLFW")
        window = GLFW.glfwCreateWindow(width * scale, height * scale, "Game Boy LCD", 0, 0)
        if (window == 0L) throw RuntimeException("Failed to create GLFW window")
        GLFW.glfwMakeContextCurrent(window)
        GL.createCapabilities()
        GL11.glClearColor(0f, 0f, 0f, 0f)

        textureId = GL11.glGenTextures()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST)
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST)
        GL11.glTexImage2D(
            GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, width, height, 0,
            GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, null as java.nio.ByteBuffer?
        )
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
        pixels.copyInto(frameBuffer)
        dirty = true // Mark the frame buffer as dirty
    }

    fun updateTexture() {
        pixelBuffer.clear()
        for (i in frameBuffer.indices) {
            val shade = frameBuffer[i].toInt() and 0xFF
            val color = when (shade) {
                0 -> white
                1 -> lightGray
                2 -> darkGray
                else -> black
            }
            pixelBuffer.put((color[0] * 255).toInt().toByte())
            pixelBuffer.put((color[1] * 255).toInt().toByte())
            pixelBuffer.put((color[2] * 255).toInt().toByte())
        }
        pixelBuffer.flip()
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
        GL11.glTexSubImage2D(
            GL11.GL_TEXTURE_2D, 0, 0, 0, width, height,
            GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, pixelBuffer
        )
    }

    fun refresh() {
        if (!dirty) return // Only render if the frame buffer has changed

        GLFW.glfwPollEvents()
        checkInput()
        dirty = false

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
        updateTexture()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)
        GL11.glBegin(GL11.GL_QUADS)
        GL11.glTexCoord2f(0f, 1f); GL11.glVertex2f(-1f, -1f)
        GL11.glTexCoord2f(1f, 1f); GL11.glVertex2f(1f, -1f)
        GL11.glTexCoord2f(1f, 0f); GL11.glVertex2f(1f, 1f)
        GL11.glTexCoord2f(0f, 0f); GL11.glVertex2f(-1f, 1f)
        GL11.glEnd()
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GLFW.glfwSwapBuffers(window)
    }

    fun shouldClose(): Boolean {
        return GLFW.glfwWindowShouldClose(window)
    }

    fun dispose() {
        GLFW.glfwDestroyWindow(window)
        GLFW.glfwTerminate()
    }

    companion object {
/*        val black = floatArrayOf(0f, 0f, 0f)
        val white = floatArrayOf(1f, 1f, 1f)
        val lightGray = floatArrayOf(0.7f, 0.7f, 0.7f)
        val darkGray = floatArrayOf(0.4f, 0.4f, 0.4f)*/
        val black = floatArrayOf(0.15f, 0.18f, 0.22f)
        val white = floatArrayOf(0.85f, 0.88f, 0.80f)
        val lightGray = floatArrayOf(0.60f, 0.68f, 0.60f)
        val darkGray = floatArrayOf(0.35f, 0.42f, 0.38f)
    }
}
