# OCR Pipeline Documentation

## Overview
The OCR pipeline in LOT operates strictly offline using on-device text block detection:
1. `OfflineOcrEngine.detectTextRegions(bitmap)` receives an input bitmap.
2. Extracts bounding coordinates (`RectF`), text lines, script classification, and confidence.
3. Groups text blocks hierarchically to maintain sentence coherence.
4. Passes coordinates and extracted text to `VisualTextReplacer`.
