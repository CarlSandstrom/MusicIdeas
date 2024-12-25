package com.musicideas

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicideas.audio.JavaSoundEngine
import com.musicideas.audio.recording.JavaSoundRecorder
import com.musicideas.ui.components.MetronomeView
import com.musicideas.ui.components.VolumeGauge
import com.musicideas.ui.components.MetronomeViewModel
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

@Composable
@Preview
fun App() {
    var isRecording by remember { mutableStateOf(false) }
    val audioEngine = remember { JavaSoundEngine() }
    val recorder = remember { JavaSoundRecorder() }
    val metronomeViewModel = remember { MetronomeViewModel(audioEngine) }

    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    if (isRecording) {
                        recorder.stopRecording()
                    } else {
                        recorder.startRecording()
                    }
                    isRecording = !isRecording
                }
            ) {
                Text(if (isRecording) "Stop Recording" else "Start Recording")
            }

            VolumeGauge(recorder)
            MetronomeView(metronomeViewModel)
        }
    }
}

// At the bottom of App.kt, after your App() composable
fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Music Ideas") {
        App()
    }
}
