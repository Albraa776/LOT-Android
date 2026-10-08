# Continuous Offline Speech Translation Pipeline

## Overview
1. **Audio Recording**: `AudioRecorder` initiates continuous capture via Android `MediaRecorder` into AAC/M4A format. Live amplitude updates stream to `AudioWaveform` at 100ms intervals.
2. **Offline ASR**: Audio is decoded locally without network connections.
3. **Neural Translation**: Decoded transcript is processed by `HyMt2TranslationEngine`.
4. **Playback**: Both transcript and translation can be spoken aloud via `OfflineTtsEngine`.
