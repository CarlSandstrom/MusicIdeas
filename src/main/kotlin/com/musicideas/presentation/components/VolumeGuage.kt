package com.musicideas.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VolumeGauge(level: Float) {
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