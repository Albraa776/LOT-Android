package com.albraa.lot.ui.image

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.albraa.lot.ui.theme.ConsoleBackground
import com.albraa.lot.ui.theme.PhosphorCyan
import kotlin.math.roundToInt

@Composable
fun ImageComparisonSlider(
    original: Bitmap,
    translated: Bitmap,
    modifier: Modifier = Modifier
) {
    var splitFraction by remember { mutableFloatStateOf(0.5f) }
    var containerWidth by remember { mutableFloatStateOf(1f) }

    Box(
        modifier = modifier
            .clipToBounds()
            .onSizeChanged { containerWidth = it.width.toFloat().coerceAtLeast(1f) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val newFrac = (splitFraction + dragAmount.x / containerWidth).coerceIn(0.02f, 0.98f)
                    splitFraction = newFrac
                }
            }
    ) {
        // Base: Translated Image
        Image(
            bitmap = translated.asImageBitmap(),
            contentDescription = "Translated Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay: Original Image clipped to split fraction
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
        ) {
            Image(
                bitmap = original.asImageBitmap(),
                contentDescription = "Original Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth(splitFraction)
                    .clipToBounds()
            )
        }

        // Draggable vertical divider handle
        Box(
            modifier = Modifier
                .offset { IntOffset((containerWidth * splitFraction).roundToInt(), 0) }
                .width(2.dp)
                .fillMaxSize()
                .background(PhosphorCyan)
        )

        // Center circular thumb indicator
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (containerWidth * splitFraction - 16.dp.toPx()).roundToInt(),
                        (this.size.height / 2 - 16.dp.toPx()).roundToInt()
                    )
                }
                .size(32.dp)
                .background(ConsoleBackground, CircleShape)
                .align(Alignment.CenterStart)
        ) {
            Icon(
                imageVector = Icons.Default.SwapHoriz,
                contentDescription = "Drag comparison slider",
                tint = PhosphorCyan,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
