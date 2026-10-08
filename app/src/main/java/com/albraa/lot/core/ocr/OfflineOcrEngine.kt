package com.albraa.lot.core.ocr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OfflineOcrEngine(private val context: Context) {

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    suspend fun detectTextRegions(bitmap: Bitmap): List<OcrRegion> = withContext(Dispatchers.Default) {
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        suspendCancellableCoroutine { continuation ->
            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    val regions = mutableListOf<OcrRegion>()
                    for (block in visionText.textBlocks) {
                        val rect = block.boundingBox
                        val rectF = if (rect != null) {
                            RectF(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat())
                        } else {
                            RectF(0f, 0f, 0f, 0f)
                        }

                        val lineTexts = block.lines.map { it.text }
                        val confidence = block.lines.mapNotNull { it.confidence }.average().toFloat().takeIf { !it.isNaN() } ?: 0.95f

                        regions.add(
                            OcrRegion(
                                boundingBox = rectF,
                                text = block.text,
                                confidence = confidence,
                                lines = lineTexts
                            )
                        )
                    }
                    continuation.resume(regions)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
}
