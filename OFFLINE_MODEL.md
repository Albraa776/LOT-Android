# Offline Model Specifications: Tencent Hunyuan Hy-MT2

## 1. Engine Details

- **Model Family**: Tencent Hunyuan Hy-MT2
- **Variant**: `Hy-MT2-1.8B` On-Device Compact Quantized
- **Architecture**: `HunYuanDenseV1ForCausalLM` / `AngelSlim` 1.25b Quantization (~440 MB on-device storage)
- **Supported Languages**: 33 languages with mutual bidirectional translation.

## 2. Official Inference Instructions

Hy-MT2 follows a fast-thinking instruction paradigm without system preambles:
```
将以下文本翻译为 <TARGET_LANGUAGE> ，注意只需要输出翻译后的结果，不要额外解释：

<SOURCE_TEXT>
```

### Hyperparameters:
- `Temperature`: 0.7
- `Top-p`: 0.6
- `Top-k`: 20
- `Repetition Penalty`: 1.05

## 3. Storage & Integrity Management

`ModelManager` monitors local storage in `context.filesDir/models/`, verifies files against expected SHA-256 signatures, and reports storage consumption in Settings.
