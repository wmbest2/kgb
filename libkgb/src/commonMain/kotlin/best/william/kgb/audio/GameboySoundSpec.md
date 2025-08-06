# Gameboy Sound Specification

This document describes the Gameboy's sound hardware, registers, channels, and behavior for emulation and development purposes.

## Overview
The original Gameboy (DMG) features a programmable sound generator with four channels:
- **Channel 1:** Square wave with sweep and envelope
- **Channel 2:** Square wave with envelope
- **Channel 3:** Programmable waveform
- **Channel 4:** Noise generator

## Sound Registers
Sound is controlled via memory-mapped registers at addresses $FF10-$FF3F. Key registers include:

| Register | Address | Channel | Description |
|----------|---------|---------|-------------|
| NR10     | $FF10   | 1       | Sweep (freq) |
| NR11     | $FF11   | 1       | Duty/Length |
| NR12     | $FF12   | 1       | Envelope |
| NR13     | $FF13   | 1       | Freq low |
| NR14     | $FF14   | 1       | Freq high/trigger |
| NR21     | $FF16   | 2       | Duty/Length |
| NR22     | $FF17   | 2       | Envelope |
| NR23     | $FF18   | 2       | Freq low |
| NR24     | $FF19   | 2       | Freq high/trigger |
| NR30     | $FF1A   | 3       | DAC power |
| NR31     | $FF1B   | 3       | Length |
| NR32     | $FF1C   | 3       | Output level |
| NR33     | $FF1D   | 3       | Freq low |
| NR34     | $FF1E   | 3       | Freq high/trigger |
| NR41     | $FF20   | 4       | Length |
| NR42     | $FF21   | 4       | Envelope |
| NR43     | $FF22   | 4       | Polynomial counter |
| NR44     | $FF23   | 4       | Counter/consecutive |
| NR50     | $FF24   | All     | Vin/panning |
| NR51     | $FF25   | All     | Channel panning |
| NR52     | $FF26   | All     | Sound on/off |

## Channel Details
### Channel 1: Square Wave + Sweep
- Frequency sweep for pitch effects
- Envelope for volume control
- 4 duty cycles: 12.5%, 25%, 50%, 75%

### Channel 2: Square Wave
- Envelope for volume control
- 4 duty cycles

### Channel 3: Wave Channel
- 32 4-bit samples (wave RAM)
- Output level control

### Channel 4: Noise Channel
- Linear feedback shift register (LFSR) for noise
- Envelope for volume
- Adjustable frequency

## Sound Output
- Stereo panning via NR50/NR51
- Master enable via NR52
- Output is mixed and sent to DAC

## Timing
- Sound length counters decrement at 256 Hz
- Envelope steps at 64 Hz
- Sweep steps at 128 Hz

## References
- [Pan Docs: Sound](https://gbdev.io/pandocs/Sound.html)
- [Gameboy CPU Manual](../reference/gbcpuman.pdf)
- [DMG_ROM.asm](../reference/DMG_ROM.asm)

## Notes
- Sound hardware is disabled in STOP mode
- Writing to NR52 can disable all sound
- Channel 3 waveform RAM is at $FF30-$FF3F

---
This document is a summary for emulator development. For full details, see Pan Docs and official manuals.

