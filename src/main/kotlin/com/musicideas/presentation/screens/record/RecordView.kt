package com.musicideas.presentation.screens.record

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.musicideas.presentation.components.VolumeGauge
import com.musicideas.presentation.components.WaveformView

@Composable
fun RecordView(viewModel: RecordViewModel, onSave: (ByteArray) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                val waveformData = when {
                    viewModel.isRecording -> viewModel.liveBuffer
                    viewModel.audioData != null -> viewModel.audioData!!
                    else -> ByteArray(0)
                }
                WaveformView(
                    audioData = waveformData,
                    currentTimeMs = viewModel.currentTimeMs
                )
            }

            Box(modifier = Modifier.width(120.dp)) {
                VolumeGauge(viewModel, viewModel.isRecording, viewModel.isPlaying)
            }
        }

        viewModel.errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        if (viewModel.isRecording) {
            RecordingIndicator()
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { viewModel.startPlayback() },
                enabled = !viewModel.isPlaying && !viewModel.isRecording && viewModel.audioData != null
            ) {
                Text("Play")
            }

            Button(
                onClick = { viewModel.startRecording() },
                enabled = !viewModel.isRecording && !viewModel.isPlaying
            ) {
                Text("Record")
            }

            Button(
                onClick = {
                    viewModel.stopRecording()
                    viewModel.stopPlayback()
                },
                enabled = viewModel.isRecording || viewModel.isPlaying
            ) {
                Text("Stop")
            }

            Button(
                onClick = { viewModel.audioData?.let { onSave(it) } },
                enabled = !viewModel.isRecording && !viewModel.isPlaying && viewModel.audioData != null
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun RecordingIndicator() {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Canvas(modifier = Modifier.size(12.dp)) {
            drawCircle(Color.Red.copy(alpha = alpha))
        }
        Text("REC", color = Color.Red, fontWeight = FontWeight.Bold)
    }
}
