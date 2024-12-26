package com.musicideas.ui.screens.record

import androidx.compose.foundation.background
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

@Composable
fun RecordView(
    viewModel: RecordViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Record New Idea",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Waveform and volume gauge container
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Waveform placeholder
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(200.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Waveform Visualization",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            }

            // Volume gauge placeholder
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(200.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                VolumeGauge(viewModel.getAudioRecorder())
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Playback and recording controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cursor backward button
            IconButton(
                onClick = { viewModel.moveCursorBackward() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Move Cursor Backward",
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Play/Pause button
            IconButton(
                onClick = { viewModel.togglePlayback() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (viewModel.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (viewModel.isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Cursor forward button
            IconButton(
                onClick = { viewModel.moveCursorForward() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Move Cursor Forward",
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            // Record button
            IconButton(
                onClick = {
                    if (viewModel.isRecording) {
                        viewModel.stopRecording()
                    } else {
                        viewModel.startRecording()
                    }
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = if (viewModel.isRecording) Icons.Default.Stop else Icons.Default.FiberManualRecord,
                    contentDescription = if (viewModel.isRecording) "Stop Recording" else "Start Recording",
                    tint = if (viewModel.isRecording) Color.Red else Color.Red,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Recording info
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // val duration by viewModel.recordingDuration.collectAsState()
            val duration = 0.0

            Text(
                text = if (viewModel.isRecording) "Recording..." else "Ready to record",
                style = MaterialTheme.typography.subtitle1
            )

            Text(
                text = "Duration: ${duration / 1000}s",
                style = MaterialTheme.typography.body2
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tags input
        OutlinedTextField(
            value = "",
            onValueChange = { /* TODO: Implement tags handling */ },
            label = { Text("Add tags (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
