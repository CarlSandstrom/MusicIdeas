package com.musicideas.ui.screens.record

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.musicideas.ui.components.VolumeGauge
import com.musicideas.ui.components.WaveformView

@Composable
fun RecordView(viewModel: RecordViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Volume gauge

        Row(modifier = Modifier.weight(0.5f)) {
            // Waveform display
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .border(width = 1.dp,
                    color = Color.Gray,
                    shape = RoundedCornerShape(4.dp)
                    )
            ) {
                viewModel.audioData?.let { audioData ->
                    WaveformView(
                        audioData = audioData,
                        currentTimeMs = viewModel.currentTimeMs,
                    )
                }
            }
            Box (modifier = Modifier
                .width(100.dp)
                .border(width = 1.dp,
                color = Color.Gray,
                shape = RoundedCornerShape(4.dp)
            )
            ) {
                VolumeGauge(viewModel.getAudioRecorder())
            }
        }

        // Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    if (viewModel.isRecording) {
                        viewModel.stopRecording()
                    } else {
                        viewModel.startRecording()
                    }
                }
            ) {
                Text(if (viewModel.isRecording) "Stop Recording" else "Start Recording")
            }

            Button(
                onClick = { viewModel.startPlayback() },
                enabled = viewModel.audioData != null && !viewModel.isRecording
            ) {
                Text("Play")
            }

            Button(
                onClick = { viewModel.stopPlayback() },
                enabled = viewModel.audioData != null && !viewModel.isRecording
            ) {
                Text("Stop")
            }
        }
    }
}