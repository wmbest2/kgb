package best.william.kgb.rom

import kgb.rom.Rom
import kgb.rom.toRom
import java.io.File


@ExperimentalUnsignedTypes
fun File.loadAsRom(): Rom {
    return this.readBytes().toRom()
}