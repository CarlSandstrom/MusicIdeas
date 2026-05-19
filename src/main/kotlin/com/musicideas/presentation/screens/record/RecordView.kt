package com.musicideas.presentation.screens.record

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.musicideas.presentation.components.VolumeGauge
import com.musicideas.presentation.components.WaveformView
import java.awt.KeyboardFocusManager

@Composable
fun RecordView(viewModel: RecordViewModel, onSave: (ByteArray, Int) -> Unit) {
    val onSaveUpdated = rememberUpdatedState(onSave)

    DisposableEffect(Unit) {
        val dispatcher = java.awt.KeyEventDispatcher { e ->
            if (e.id != java.awt.event.KeyEvent.KEY_PRESSED) return@KeyEventDispatcher false
            when (e.keyCode) {
                java.awt.event.KeyEvent.VK_SPACE -> when {
                    viewModel.isRecording -> { viewModel.stopRecording(); true }
                    !viewModel.isPlaying && viewModel.audioData == null -> { viewModel.startRecording(); true }
                    else -> false
                }
                java.awt.event.KeyEvent.VK_ENTER -> {
                    if (!viewModel.isPlaying && !viewModel.isRecording && viewModel.audioData != null) {
                        viewModel.startPlayback(); true
                    } else false
                }
                java.awt.event.KeyEvent.VK_DELETE, java.awt.event.KeyEvent.VK_BACK_SPACE -> {
                    if (!viewModel.isRecording && !viewModel.isPlaying && viewModel.audioData != null) {
                        viewModel.discardRecording(); true
                    } else false
                }
                java.awt.event.KeyEvent.VK_ESCAPE -> when {
                    viewModel.isRecording -> { viewModel.stopRecording(); true }
                    viewModel.isPlaying -> { viewModel.stopPlayback(); true }
                    else -> false
                }
                java.awt.event.KeyEvent.VK_S -> {
                    if (e.isControlDown && !viewModel.isRecording && !viewModel.isPlaying && viewModel.audioData != null) {
                        viewModel.audioData?.let { audio ->
                            viewModel.stopMetronome()
                            onSaveUpdated.value(audio, viewModel.recordingTempo)
                        }
                        true
                    } else false
                }
                else -> false
            }
        }
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher)
        onDispose {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dispatcher)
        }
    }

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

        MetronomeControls(viewModel)

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
                onClick = { viewModel.discardRecording() },
                enabled = !viewModel.isRecording && !viewModel.isPlaying && viewModel.audioData != null
            ) {
                Text("Discard")
            }

            Button(
                onClick = {
                    viewModel.audioData?.let { audio ->
                        val tempo = viewModel.recordingTempo
                        viewModel.stopMetronome()
                        onSave(audio, tempo)
                    }
                },
                enabled = !viewModel.isRecording && !viewModel.isPlaying && viewModel.audioData != null
            ) {
                Text("Save")
            }
        }
    }
}

@Composable
private fun MetronomeControls(viewModel: RecordViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(onClick = { viewModel.toggleMetronome() }) {
            Text(if (viewModel.metronomeEnabled) "Metronome: ON" else "Metronome: OFF")
        }

        Button(
            onClick = { viewModel.updateMetronomeBpm(viewModel.metronomeBpm - 5) },
            enabled = viewModel.metronomeBpm > 40
        ) {
            Text("-")
        }

        Text(
            text = "${viewModel.metronomeBpm} BPM",
            modifier = Modifier.width(72.dp),
            textAlign = TextAlign.Center
        )

        Button(
            onClick = { viewModel.updateMetronomeBpm(viewModel.metronomeBpm + 5) },
            enabled = viewModel.metronomeBpm < 240
        ) {
            Text("+")
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
