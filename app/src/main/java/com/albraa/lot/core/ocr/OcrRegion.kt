package com.albraa.lot.core.ocr

import android.graphics.RectF

data class OcrRegion(
    val boundingBox: RectF,
    val text: String,
    val confidence: Float = 1.0f,
    val angleDegrees: Float = 0f,
    val lines: List<String> = emptyList()
)
