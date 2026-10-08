package com.albraa.lot.core.inpainting

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.albraa.lot.core.model.ImageTranslationRegion
import com.albraa.lot.core.model.Language
import com.albraa.lot.core.model.TextDirection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max
import kotlin.math.min

class VisualTextReplacer {

    suspend fun replaceTextInImage(
        originalBitmap: Bitmap,
        regions: List<ImageTranslationRegion>,
        targetLanguage: Language
    ): Bitmap = withContext(Dispatchers.Default) {
        // Create an ARGB_8888 mutable copy of the bitmap
        val outputBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(outputBitmap)

        for (region in regions) {
            if (!region.isVisible) continue
            val textToRender = if (region.translatedText.isNotBlank()) region.translatedText else region.sourceText
            if (textToRender.isBlank()) continue

            val box = region.boundingBox
            val left = max(0, box.left.toInt())
            val top = max(0, box.top.toInt())
            val right = min(outputBitmap.width, box.right.toInt())
            val bottom = min(outputBitmap.height, box.bottom.toInt())

            val width = right - left
            val height = bottom - top
            if (width <= 0 || height <= 0) continue

            // 1. Sample surrounding background color
            val bgColor = sampleSurroundingBackgroundColor(outputBitmap, left, top, right, bottom)
            
            // 2. Determine optimal text color based on luminance
            val isLightBg = isColorLight(bgColor)
            val textColor = if (isLightBg) Color.BLACK else Color.WHITE

            // 3. Inpaint / erase original text area
            val inpaintPaint = Paint().apply {
                color = bgColor
                style = Paint.Style.FILL
                isAntiAlias = true
            }
            canvas.drawRect(RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat()), inpaintPaint)

            // 4. Render translated text with proper direction, font size, and wrapping
            renderTextInBox(
                canvas = canvas,
                text = textToRender,
                box = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat()),
                textColor = textColor,
                isRtl = targetLanguage.direction == TextDirection.RTL,
                rotationDegrees = region.rotationDegrees
            )
        }

        outputBitmap
    }

    private fun sampleSurroundingBackgroundColor(bitmap: Bitmap, left: Int, top: Int, right: Int, bottom: Int): Int {
        var rSum = 0L
        var gSum = 0L
        var bSum = 0L
        var count = 0

        val stepX = max(1, (right - left) / 10)
        val stepY = max(1, (bottom - top) / 10)

        // Sample top edge
        val sampleTop = max(0, top - 2)
        for (x in left until right step stepX) {
            val pixel = bitmap.getPixel(x, sampleTop)
            rSum += Color.red(pixel)
            gSum += Color.green(pixel)
            bSum += Color.blue(pixel)
            count++
        }

        // Sample bottom edge
        val sampleBottom = min(bitmap.height - 1, bottom + 1)
        for (x in left until right step stepX) {
            val pixel = bitmap.getPixel(x, sampleBottom)
            rSum += Color.red(pixel)
            gSum += Color.green(pixel)
            bSum += Color.blue(pixel)
            count++
        }

        // Sample left edge
        val sampleLeft = max(0, left - 2)
        for (y in top until bottom step stepY) {
            val pixel = bitmap.getPixel(sampleLeft, y)
            rSum += Color.red(pixel)
            gSum += Color.green(pixel)
            bSum += Color.blue(pixel)
            count++
        }

        // Sample right edge
        val sampleRight = min(bitmap.width - 1, right + 1)
        for (y in top until bottom step stepY) {
            val pixel = bitmap.getPixel(sampleRight, y)
            rSum += Color.red(pixel)
            gSum += Color.green(pixel)
            bSum += Color.blue(pixel)
            count++
        }

        if (count == 0) return Color.WHITE
        return Color.rgb((rSum / count).toInt(), (gSum / count).toInt(), (bSum / count).toInt())
    }

    private fun isColorLight(color: Int): Boolean {
        val darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness < 0.5
    }

    private fun renderTextInBox(
        canvas: Canvas,
        text: String,
        box: RectF,
        textColor: Int,
        isRtl: Boolean,
        rotationDegrees: Float
    ) {
        val targetWidth = box.width()
        val targetHeight = box.height()

        canvas.save()
        if (rotationDegrees != 0f) {
            canvas.rotate(rotationDegrees, box.centerX(), box.centerY())
        }

        val textPaint = TextPaint().apply {
            color = textColor
            isAntiAlias = true
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        // Calculate dynamic optimal font size
        var fontSize = max(14f, targetHeight * 0.75f)
        var layout: StaticLayout
        val alignment = if (isRtl) Layout.Alignment.ALIGN_OPPOSITE else Layout.Alignment.ALIGN_NORMAL

        do {
            textPaint.textSize = fontSize
            layout = StaticLayout.Builder.obtain(text, 0, text.length, textPaint, targetWidth.toInt().coerceAtLeast(10))
                .setAlignment(alignment)
                .setIncludePad(false)
                .build()

            if (layout.height <= targetHeight && layout.width <= targetWidth) {
                break
            }
            fontSize -= 2f
        } while (fontSize > 10f)

        // Center vertically inside bounding box
        val verticalOffset = box.top + max(0f, (targetHeight - layout.height) / 2f)

        canvas.save()
        canvas.translate(box.left, verticalOffset)
        layout.draw(canvas)
        canvas.restore()

        canvas.restore()
    }
}
