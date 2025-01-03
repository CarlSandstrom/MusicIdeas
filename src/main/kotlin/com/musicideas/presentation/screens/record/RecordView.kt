package com.musicideas.presentation.screens.record

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                viewModel.audioData?.let { audioData ->
                    WaveformView(
                        audioData = audioData,
                        currentTimeMs = viewModel.currentTimeMs
                    )
                }
            }

            Box(modifier = Modifier.width(100.dp)) {
                VolumeGauge(viewModel)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    if (viewModel.isRecording) viewModel.stopRecording()
                    else viewModel.startRecording()
                },
                enabled = !viewModel.isPlaying
            ) {
                Text(if (viewModel.isRecording) "Stop" else "Record")
            }

            Button(
                onClick = {
                    if (viewModel.isPlaying) viewModel.stopPlayback()
                    else viewModel.startPlayback()
                },
                enabled = viewModel.audioData != null && !viewModel.isRecording
            ) {
                Text(if (viewModel.isPlaying) "Stop" else "Play")
            }

            Button(
                onClick = { viewModel.audioData?.let { onSave(it) }},
                enabled = (!viewModel.isRecording) && (!viewModel.isPlaying) && (viewModel.audioData != null)
            ) {
                Text("Save")
            }
        }
    }
}