package com.musicideas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicideas.ui.components.metronome.MetronomeController

@Composable
fun MetronomeControl(metronomeController: MetronomeController) {
    var bpm by remember { mutableStateOf(120) }
    var isPlaying by remember { mutableStateOf(false) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("BPM:")
            Slider(
                value = bpm.toFloat(),
                onValueChange = {
                    bpm = it.toInt()
                    metronomeController.setBPM(bpm)
                },
                valueRange = 40f..208f
            )
            Text(bpm.toString())
        }

        Button(onClick = {
            if (isPlaying) {
                metronomeController.stop()
            } else {
                metronomeController.start()
            }
            isPlaying = !isPlaying
        }) {
            Text(if (isPlaying) "Stop" else "Start")
        }
    }
}
