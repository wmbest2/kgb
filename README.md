# KGB - Kotlin Game Boy Emulator

A Game Boy (DMG) emulator written in Kotlin Multiplatform, supporting JVM, Linux, and Windows platforms.

## Overview

KGB is a Game Boy emulator that emulates the original Game Boy (DMG) hardware, including:
- **LR35902 CPU** - Sharp's custom 8-bit processor
- **Graphics (PPU)** - Picture Processing Unit with support for sprites, backgrounds, and window
- **Audio (APU)** - All 4 sound channels (2 pulse waves, 1 wave channel, 1 noise channel)
- **Memory Banking Controllers** - MBC1 and MBC3 support
- **Input** - Full controller support

The project is built using Kotlin Multiplatform, enabling it to run on multiple targets:
- **JVM** - Uses LWJGL for graphics (OpenGL) and audio (OpenAL)
- **Linux (Native)** - Native x64 binary
- **Windows (Native)** - Native MinGW x64 binary

## Features

- ✅ Full LR35902 CPU instruction set
- ✅ LCD/PPU with sprite and background rendering
- ✅ Audio Processing Unit (APU) with all 4 channels
- ✅ MBC1 and MBC3 cartridge support
- ✅ Real-time emulation with frame limiting
- ✅ Controller input support
- ✅ Cross-platform support (JVM, Linux, Windows)

## Building

### Prerequisites

- JDK 11 or higher
- Gradle (included via wrapper)

### Build All Platforms

```bash
./gradlew build
```

### Build Platform-Specific Binaries

**JVM:**
```bash
./gradlew :libkgb:jvmJar
```

**Linux Native:**
```bash
./gradlew :libkgb:linuxX64Binaries
```

**Windows Native:**
```bash
./gradlew :libkgb:windowsBinaries
```

## Usage

### Running on JVM

The emulator requires two files:
1. **Boot ROM** (`DMG_ROM.bin`) - The original Game Boy BIOS
2. **Game ROM** - A Game Boy cartridge file (`.gb`)

**Basic Usage:**

```kotlin
import best.william.kgb.Gameboy
import best.william.kgb.lcd.LWJGLRenderer
import best.william.kgb.audio.OpenALSpeaker
import best.william.kgb.rom.loadCartridge
import java.io.File

// Load boot ROM and cartridge
val bootRom = File("path/to/DMG_ROM.bin").readBytes()
val cartridge = File("path/to/game.gb").loadCartridge()

// Create renderer and audio
val renderer = LWJGLRenderer()
val speaker = OpenALSpeaker()

// Initialize Game Boy
val gameboy = Gameboy(
    bootRom = bootRom,
    renderer = renderer,
    speaker = speaker,
    controller = renderer,
    enableFrameLimiter = true
)

// Load and run
gameboy.loadCartridge(cartridge)
gameboy.run { rendered, exit ->
    renderer.refresh()
    if (renderer.shouldClose()) exit()
}
```

**Run the example application:**

Update the file paths in `libkgb/src/jvmMain/kotlin/best/william/kgb/App.kt`, then:

```bash
./gradlew :libkgb:runJvm
```

### Running Tests

```bash
./gradlew test
```

## Project Structure

```
kgb/
├── libkgb/                     # Main emulator library
│   └── src/
│       ├── commonMain/         # Platform-independent code
│       │   └── kotlin/best/william/kgb/
│       │       ├── Gameboy.kt          # Main emulator class
│       │       ├── cpu/                # LR35902 CPU implementation
│       │       ├── lcd/                # Picture Processing Unit
│       │       ├── audio/              # Audio Processing Unit
│       │       ├── memory/             # Memory management
│       │       ├── rom/                # Cartridge/MBC support
│       │       ├── controller/         # Input handling
│       │       └── util/               # Utilities
│       ├── commonTest/         # Shared tests
│       ├── jvmMain/            # JVM-specific code (LWJGL)
│       ├── linuxX64Main/       # Linux native code
│       └── windowsMain/        # Windows native code
└── reference/                  # Reference documentation and ROMs
```

## Technical Details

### CPU (LR35902)
The emulator implements the Sharp LR35902 processor, which is similar to a Zilog Z80 but with some differences:
- 8-bit data bus and 16-bit address bus
- Custom instruction set (subset of Z80)
- Clock speed: 4.194304 MHz
- Interrupt support (VBlank, LCD STAT, Timer, Serial, Joypad)

### Graphics (PPU)
- Resolution: 160x144 pixels
- 4 shades of gray (original DMG palette)
- Sprites: Up to 40 sprites, 10 per scanline
- Background and window layers
- Frame rate: ~59.7 Hz

### Audio (APU)
- **Channel 1**: Pulse wave with sweep
- **Channel 2**: Pulse wave
- **Channel 3**: Wave table
- **Channel 4**: Noise
- Stereo output with individual channel panning

### Memory Banking
- **MBC1**: Up to 2MB ROM, 32KB RAM
- **MBC3**: Up to 2MB ROM, 64KB RAM, Real-Time Clock support

## Roadmap

### Short-term Goals
- [ ] **Save state support** - Allow saving and loading emulator state
- [ ] **Battery-backed RAM persistence** - Save cartridge RAM to disk
- [ ] **Improved accuracy** - Fix edge cases and timing issues
- [ ] **Debugger interface** - CPU debugger with breakpoints and memory inspection
- [ ] **Configuration system** - Customizable keybindings and settings

### Medium-term Goals
- [ ] **Game Boy Color support** - Extend to support GBC features
  - Color palettes
  - Double-speed mode
  - Extended memory
- [ ] **Additional MBC support** - MBC2, MBC5, MBC7
- [ ] **Screen recording** - Export gameplay as video
- [ ] **Rewind functionality** - Rewind gameplay for instant replays
- [ ] **Netplay/Link cable emulation** - Multiplayer support

### Long-term Goals
- [ ] **Super Game Boy features** - Border support, enhanced palettes
- [ ] **Performance optimizations** - JIT compilation, shader optimizations
- [ ] **Mobile platform support** - Android and iOS targets
- [ ] **Web support** - Kotlin/JS target with WebGL rendering
- [ ] **Enhanced audio** - Audio filters, custom sound profiles
- [ ] **Tool-assisted speedrun (TAS) features** - Frame advance, input recording/playback
- [ ] **Accuracy improvements** - Pass test ROMs (blargg, mooneye, etc.)

## Dependencies

### Common
- Kotlin 2.2.10-RC
- kotlinx-coroutines-core 1.10.2
- Kermit (logging) 2.0.0

### JVM
- LWJGL 3.3.3 (OpenGL, GLFW, OpenAL)

## Contributing

Contributions are welcome! Areas that need work:
- CPU accuracy and edge cases
- PPU timing and rendering accuracy
- APU audio quality and accuracy
- Additional MBC implementations
- Test coverage
- Documentation

## License

This project is open source. Please check the repository for specific license details.

## References

- [Pan Docs](https://gbdev.io/pandocs/) - Comprehensive Game Boy technical documentation
- [Game Boy CPU Manual](http://marc.rawer.de/Gameboy/Docs/GBCPUman.pdf)
- [LWJGL](https://www.lwjgl.org/) - Lightweight Java Game Library
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)

## Acknowledgments

Built with passion for retro gaming and emulation development. Special thanks to the Game Boy development community for their extensive documentation and resources.
