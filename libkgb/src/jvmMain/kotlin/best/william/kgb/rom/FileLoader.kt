package best.william.kgb.rom

import kgb.rom.Cartridge
import kgb.rom.loadCartridge
import java.io.File


@ExperimentalUnsignedTypes
fun File.loadCartridge(): Cartridge {
    return this.readBytes().loadCartridge()
}