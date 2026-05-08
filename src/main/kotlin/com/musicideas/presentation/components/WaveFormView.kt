// presentation/components/WaveformView.kt
package com.musicideas.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun WaveformView(
    audioData: ByteArray,
    currentTimeMs: Float,
    modifier: Modifier = Modifier
) {
    Box (
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
            .padding(4.dp)
    ) {
        Canvas(modifier = modifier.fillMaxSize()) {
            if (audioData.size < 2) return@Canvas
            val samplesPerPixel = (audioData.size / 2 / size.width).toInt().coerceAtLeast(1)
            val amplitudes = getAmplitudes(audioData, samplesPerPixel)
            val peak = amplitudes.maxOrNull() ?: 1f
            val normalized = if (peak > 0f) amplitudes.map { it / peak } else amplitudes

            drawWaveform(normalized, Color.Blue)

            val totalDurationMs = audioData.size / (44100f * 2) * 1000f
            val playheadX = currentTimeMs / totalDurationMs * size.width
            if (playheadX in 0f..size.width) {
                drawLine(
                    Color.Red,
                    Offset(playheadX, 0f),
                    Offset(playheadX, size.height),
                    strokeWidth = 2f
                )
            }
        }
    }
}

private fun getAmplitudes(audioData: ByteArray, samplesPerPixel: Int): List<Float> {
    return audioData.asSequence()
        .chunked(samplesPerPixel * 2)
        .map { chunk ->
            chunk
                .chunked(2)
                .mapNotNull { bytes ->
                    if (bytes.size >= 2) {
                        val sample = (bytes[1].toInt() shl 8) or (bytes[0].toInt() and 0xFF)
                        abs(sample / 32768f)
                    } else null
                }
                .maxOrNull() ?: 0f
        }.toList()
}

private fun DrawScope.drawWaveform(amplitudes: List<Float>, color: Color) {
    val path = Path()
    val centerY = size.height / 2
    val heightScale = size.height / 2

    if (amplitudes.isEmpty()) return

    drawLine(color, Offset(0f, centerY), Offset(size.width, centerY), strokeWidth = 1f)

    path.moveTo(0f, centerY)
    amplitudes.forEachIndexed { index, amplitude ->
        val x = index * size.width / amplitudes.size
        val y = centerY + amplitude * heightScale
        path.lineTo(x, y)
    }

    amplitudes.asReversed().forEachIndexed { index, amplitude ->
        val x = size.width - index * size.width / amplitudes.size
        val y = centerY - amplitude * heightScale
        path.lineTo(x, y)
    }

    path.close()
    drawPath(path, color)
}