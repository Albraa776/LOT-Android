package com.albraa.lot.core.model

import android.graphics.RectF

data class ImageTranslationRegion(
    val id: String = java.util.UUID.randomUUID().toString(),
    val boundingBox: RectF,
    val sourceText: String,
    var translatedText: String = "",
    val confidence: Float = 1.0f,
    val rotationDegrees: Float = 0f,
    val estimatedTextColor: Int = 0xFF000000.toInt(),
    val estimatedBackgroundColor: Int = 0xFFFFFFFF.toInt(),
    var isUserEdited: Boolean = false,
    var isVisible: Boolean = true
)
