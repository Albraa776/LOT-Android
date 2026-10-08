# Offline Text-To-Speech (TTS) Pipeline

## Overview
1. **Engine**: `OfflineTtsEngine` wraps Android's native TextToSpeech engine configured strictly in offline mode.
2. **Default Voice**: **Male** voice by default, with option to select **Female** voice in Settings.
3. **Controls**: Speech rate adjustable from 0.5x to 2.0x, pitch adjustable, immediate playback cancellation and queue control.
