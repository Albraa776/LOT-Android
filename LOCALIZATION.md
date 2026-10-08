# Localization and Directionality (BiDi)

## 1. Supported UI Languages
- **English** (`res/values/strings.xml`): Primary LTR interface.
- **Arabic** (`res/values-ar/strings.xml`): Primary RTL interface.

## 2. Directionality Engine
- **Android Compose LayoutDirection**: Set at the root of `MainActivity` via `CompositionLocalProvider(LocalLayoutDirection provides layoutDirection)`.
- **RTL Languages Supported**: Arabic (`ar`), Persian (`fa`), Urdu (`ur`), Hebrew (`he`), Uyghur (`ug`).
- **Text Alignment**: Text fields dynamically align according to the respective language's directionality (`TextAlign.Right` for RTL, `TextAlign.Left` for LTR).
- **Arabic Glyph Shaping**: Preserves connected ligatures and correct cursive layout across both text translation and canvas inpainting.
