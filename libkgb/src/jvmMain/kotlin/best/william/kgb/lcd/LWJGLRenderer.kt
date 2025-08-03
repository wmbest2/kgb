package best.william.kgb.lcd

import kgb.lcd.LCDRenderer
import org.lwjgl.BufferUtils
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import kotlin.experimental.and

class LWJGLRenderer : LCDRenderer {
    private var window: Long = 0
    private val width = 160
    private val height = 144
    private val scale = 4 // Scale up for visibility

    init {
        if (!GLFW.glfwInit()) throw RuntimeException("Unable to initialize GLFW")
        window = GLFW.glfwCreateWindow(width * scale, height * scale, "Game Boy LCD", 0, 0)
        if (window == 0L) throw RuntimeException("Failed to create GLFW window")
        GLFW.glfwMakeContextCurrent(window)
        GL.createCapabilities()
        GL11.glClearColor(1f, 1f, 1f, 1f)
    }

    override fun render(pixels: UByteArray) {
        GLFW.glfwPollEvents()
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
        GL11.glBegin(GL11.GL_POINTS)
        GL11.glBegin(GL11.GL_POINTS)
        GL11.glBegin(GL11.GL_POINTS)
        GL11.glBegin(GL11.GL_POINTS)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val idx = y * width + x
                val shade = pixels[idx].toInt() and 0xFF
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

    fun shouldClose(): Boolean = GLFW.glfwWindowShouldClose(window)

    fun dispose() {
        GLFW.glfwDestroyWindow(window)
        GLFW.glfwTerminate()
    }
}
