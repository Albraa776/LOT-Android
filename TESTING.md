# Testing Framework and Test Plan

## Unit Testing
- `LanguageRegistryTest`: Verifies directionality classifications (Arabic/Hebrew as RTL, English as LTR), default Male voice attributes, and capability lookups.
- `HyMt2TranslationEngineTest`: Verifies prompt construction syntax conforming to Tencent Hunyuan's instructions, format preservation, and bidirectional dictionary mappings.

## Offline Functional Validation
1. Enable **Airplane Mode** on the device.
2. Verify text translation operates without network permissions.
3. Verify image selection, OCR detection, inpainting, and visual replacement operate locally.
4. Verify continuous speech recording, local transcription, and offline TTS playback.
