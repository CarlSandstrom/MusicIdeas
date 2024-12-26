package com.musicideas.ui.screens.record

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicideas.ui.components.MetronomeView

@Composable
fun RecordView(viewModel: RecordViewModel = remember { RecordViewModel() }) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Record",
            style = MaterialTheme.typography.headlineMedium
        )
        // MetronomeView() // Your metronome component
    }
}