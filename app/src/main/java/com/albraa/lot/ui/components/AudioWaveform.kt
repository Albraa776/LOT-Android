package com.albraa.lot.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.albraa.lot.ui.theme.PhosphorCyan

@Composable
fun AudioWaveform(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val bars = if (amplitudes.isEmpty()) List(32) { 0.1f } else amplitudes.takeLast(32)
        for (amp in bars) {
            val animatedHeight by animateFloatAsState(
                targetValue = (amp * 56f).coerceIn(4f, 56f),
                animationSpec = tween(durationMillis = 80),
                label = "waveform_bar"
            )
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(animatedHeight.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(PhosphorCyan.copy(alpha = (amp + 0.3f).coerceIn(0.2f, 1f)))
            )
        }
    }
}
