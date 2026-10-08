# Architecture of LOT (Locally Offline Translation)

## 1. System Overview

LOT is engineered as a local-first, offline-first linguistic instrument. No user data, images, audio recordings, or translations ever leave the device.

```
+------------------------------------------------------------------+
|                           User Interface                         |
|  - Jetpack Compose UI with Instrument Design System              |
|  - Bidirectional (RTL / LTR) Layout Direction Engine             |
|  - Navigation: Text, Image, Speech, History, Settings            |
+------------------------------------------------------------------+
                                 │
                                 ▼
+------------------------------------------------------------------+
|                         Application Core                         |
|  - LanguageRegistry (Dynamic Model Capability Discovery)         |
|  - SettingsRepository (DataStore Preferences)                    |
|  - LOTDatabase (Room Database Archive)                           |
+------------------------------------------------------------------+
         │                       │                       │
         ▼                       ▼                       ▼
+-------------------+   +-------------------+   +-------------------+
|  Neural Engine    |   |    Vision Core    |   |    Audio Core     |
| - Hy-MT2 1.8B     |   | - Offline OCR     |   | - Continuous Rec  |
| - ModelManager    |   | - Inpainter       |   | - Offline ASR     |
| - Prompt Builder  |   | - Visual Replacer |   | - Offline TTS     |
+-------------------+   +-------------------+   +-------------------+
```

## 2. Dynamic Language Capability Registry

Languages are not hardcoded. The application inspects `registry/language_capabilities.json` dynamically loaded at runtime:
- Derives writing system, script, and text direction (`RTL` or `LTR`).
- Flags feature capabilities: translation, OCR, speech recognition (ASR), and text-to-speech (TTS).
- Allows drop-in configuration upgrades without altering UI components.

## 3. Visual Text Replacement Pipeline

Rather than merely displaying OCR text beneath an image, LOT replaces text directly inside the image bitmap:
1. **Detection**: `OfflineOcrEngine` extracts text blocks and bounding rectangles (`RectF`).
2. **Translation**: `HyMt2TranslationEngine` translates source text to the target language.
3. **Background Sampling & Inpainting**: `VisualTextReplacer` samples perimeter pixel colors around bounding boxes and inpaints over the original text.
4. **Geometric Typesetting**: Dynamically computes optimal text scale, applies line-wrapping, and renders target text with Canvas and Paint, respecting script directionality (RTL for Arabic/Hebrew/Urdu, LTR for Latin/CJK).

## 4. Audio & Speech Pipeline

1. **Continuous Capture**: `AudioRecorder` captures microphone audio into local AAC/M4A files until explicitly stopped by the user. Streams live amplitude floats to `AudioWaveform`.
2. **Offline Transcription**: `OfflineSpeechRecognizer` decodes audio offline into source text.
3. **Translation**: Source text is passed to `HyMt2TranslationEngine`.
4. **Offline Playback**: `OfflineTtsEngine` synthesizes speech locally with configurable male/female voices, speech rates, and pitches.
