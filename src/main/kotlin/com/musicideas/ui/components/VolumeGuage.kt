package com.musicideas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    Box(
        modifier = Modifier
            .width(200.dp)
            .height(20.dp)
            .background(Color.Gray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width((200 * level).dp)
                .background(Color.Green)
        )
    }
}