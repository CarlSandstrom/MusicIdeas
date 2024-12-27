package com.musicideas.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.min
import kotlin.math.roundToInt

// Data class to hold our window parameters
data class WaveformWindow(
    val startTimeMs: Float,
    val durationMs: Float,
    val zoomLevel: Float
)

@Composable
fun WaveformView(
    audioData: ByteArray,
    sampleRate: Int = 44100,
    currentTimeMs: Float,
    modifier: Modifier = Modifier,
    initialWindow: WaveformWindow = WaveformWindow(0f, 5000f, 1f)
) {
    var window by remember { mutableStateOf(initialWindow) }

    // Calculate samples per pixel based on zoom level and canvas width
    fun calculateSamplesPerPixel(canvasWidth: Float): Int {
        val samplesInWindow = (window.durationMs * sampleRate / 1000f).roundToInt()
        return (samplesInWindow / canvasWidth).roundToInt().coerceAtLeast(1)
    }

    // Convert byte array to amplitude values
    fun getAmplitudes(startSample: Int, endSample: Int, samplesPerPixel: Int): List<Float> {
        val amplitudes = mutableListOf<Float>()
        var i = startSample
        while (i < endSample && i < audioData.size - 1) {
            var maxAmplitude = 0f
            for (j in 0 until samplesPerPixel) {
                if (i + j >= audioData.size - 1) break
                val sample = (audioData[i + j + 1].toInt() shl 8) or (audioData[i + j].toInt() and 0xFF)
                val amplitude = sample / 32768f
                maxAmplitude = maxOf(maxAmplitude, amplitude)
            }
            amplitudes.add(maxAmplitude)
            i += samplesPerPixel * 2 // * 2 because we're reading 16-bit samples
        }
        return amplitudes
    }

    Canvas(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    window = window.copy(
                        startTimeMs = (window.startTimeMs - pan.x * window.durationMs / size.width).coerceAtLeast(0f),
                        durationMs = (window.durationMs / zoom).coerceIn(100f, 60000f)
                    )
                }
            }
    ) {
        val samplesPerPixel = calculateSamplesPerPixel(size.width)
        val startSample = ((window.startTimeMs * sampleRate) / 1000f).roundToInt()
        val endSample = ((window.startTimeMs + window.durationMs) * sampleRate / 1000f).roundToInt()

        val amplitudes = getAmplitudes(startSample, endSample, samplesPerPixel)

        // Draw background
        drawRect(Color.Black)

        // Draw waveform
        drawWaveform(amplitudes, Color.Gray)

        // Draw playhead
        val playheadX = ((currentTimeMs - window.startTimeMs) / window.durationMs * size.width)
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

private fun DrawScope.drawWaveform(amplitudes: List<Float>, color: Color) {
    val path = Path()
    val centerY = size.height / 2
    val heightScale = size.height / 2

    if (amplitudes.isEmpty()) return

    path.moveTo(0f, centerY)

    amplitudes.forEachIndexed { index, amplitude ->
        val x = index * size.width / amplitudes.size
        val y = centerY + amplitude * heightScale
        path.lineTo(x, y)
    }

    // Draw back to center
    amplitudes.asReversed().forEachIndexed { index, amplitude ->
        val x = size.width - index * size.width / amplitudes.size
        val y = centerY - amplitude * heightScale
        path.lineTo(x, y)
    }

    path.close()
    drawPath(path, color)
}

// Extension function to help with window manipulation
fun WaveformWindow.zoom(factor: Float, centerTimeMs: Float): WaveformWindow {
    val newDuration = (durationMs * factor).coerceIn(100f, 60000f)
    val durationDiff = newDuration - durationMs
    val newStartTime = startTimeMs - (durationDiff * (centerTimeMs - startTimeMs) / durationMs)
    return copy(
        startTimeMs = newStartTime.coerceAtLeast(0f),
        durationMs = newDuration
    )
}