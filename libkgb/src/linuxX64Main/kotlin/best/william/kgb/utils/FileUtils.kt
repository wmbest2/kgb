@file:OptIn(ExperimentalForeignApi::class)

package best.william.kgb.utils

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.refTo
import platform.posix.SEEK_END
import platform.posix.SEEK_SET
import platform.posix.fclose
import platform.posix.fopen
import platform.posix.fread
import platform.posix.fseek
import platform.posix.ftell
import platform.posix.perror


fun readFile(fileName: String): ByteArray {
    // Implementation for reading a file
    // Read from a file
    var buffer: ByteArray
    memScoped {
        val file = fopen(fileName, "rb") ?: run {
            perror("Error opening file for reading")
            error("Could not open file: $fileName")
        }
        fseek(file, 0, SEEK_END)
        val fileSize = ftell(file).toInt()
        fseek(file, 0, SEEK_SET)
        buffer = ByteArray(fileSize)
        val readBytes = fread(buffer.refTo(0), 1uL, buffer.size.toULong(), file)
        fclose(file)
        if (readBytes.toInt() != fileSize) {
            error("Failed to read the entire file")
        }
    }
    return buffer
}