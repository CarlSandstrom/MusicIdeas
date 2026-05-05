package com.musicideas.presentation.screens.record

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                if (viewModel.audioData != null) {
                    WaveformView(
                        audioData = viewModel.audioData!!,
                        currentTimeMs = viewModel.currentTimeMs
                    )
                } else {
                    WaveformView(
                        audioData = ByteArray(0),
                        currentTimeMs = 0f
                    )
                }
            }

            Box(modifier = Modifier.width(100.dp)) {
                VolumeGauge(viewModel)
            }
        }

        viewModel.errorMessage?.let { error ->
            Text(
                text = error,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    viewModel.startPlayback()
                },
                enabled = !viewModel.isPlaying && !viewModel.isRecording && viewModel.audioData != null
            ) {
                Text("Play")
            }

            Button(
                onClick = {
                    viewModel.startRecording()
                },
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
                onClick = { viewModel.audioData?.let { onSave(it) }},
                enabled = (!viewModel.isRecording) && (!viewModel.isPlaying) && (viewModel.audioData != null)
            ) {
                Text("Save")
            }
        }
    }
}