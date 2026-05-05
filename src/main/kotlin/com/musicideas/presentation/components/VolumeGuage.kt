package com.musicideas.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicideas.presentation.screens.record.RecordViewModel
import kotlinx.coroutines.delay
import kotlin.math.sqrt
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun VolumeGauge(viewModel: RecordViewModel, isRecording: Boolean = false) {
    var level by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            level = viewModel.getInputLevel()
            delay(50.milliseconds)
        }
    }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Input", fontSize = 11.sp)
        BoxWithConstraints(
            modifier = Modifier.weight(1f).fillMaxWidth().background(Color.Gray)
        ) {
            val displayLevel = sqrt(sqrt(level)).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((maxHeight * displayLevel).coerceIn(0.dp, maxHeight))
                    .align(Alignment.BottomCenter)
                    .background(if (isRecording) Color.Red else Color.Green)
            )
        }
    }
}
