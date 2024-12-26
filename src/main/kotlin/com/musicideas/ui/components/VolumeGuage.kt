package com.musicideas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import com.musicideas.audio.recording.AudioRecorder
import kotlinx.coroutines.delay

@Composable
fun VolumeGauge(audioRecorder: AudioRecorder) {
    var level by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            level = audioRecorder.getInputLevel()
            delay(50)
        }
    }

    BoxWithConstraints (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((maxHeight*level).coerceIn (0.dp, maxHeight))
                .align(Alignment.BottomCenter)
                .background(Color.Green)
        )
    }
}