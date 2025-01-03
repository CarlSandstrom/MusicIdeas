package com.musicideas.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.musicideas.presentation.screens.record.RecordViewModel
import kotlinx.coroutines.delay

@Composable
fun VolumeGauge(viewModel: RecordViewModel) {
    var level by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while(true) {
            level = viewModel.getInputLevel()
            delay(50) // Update every 50ms
        }
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().background(Color.Gray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height((maxHeight * level).coerceIn(0.dp, maxHeight))
                .align(Alignment.BottomCenter)
                .background(Color.Green)
        )
    }
}