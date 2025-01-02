package com.musicideas.presentation.screens.save

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicideas.domain.model.Genre
import com.musicideas.domain.model.IdeaType
import com.musicideas.domain.model.Instrument
import com.musicideas.presentation.components.ComboBox

@Composable
fun SaveView(viewModel: SaveViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ComboBox(
            label = "Genre",
            options = Genre.values().toList(),
            selectedOption = viewModel.genre,
            onOptionSelected = { viewModel.genre = it }
        )

        ComboBox(
            label = "Instrument",
            options = Instrument.values().toList(),
            selectedOption = viewModel.instrument,
            onOptionSelected = { viewModel.instrument = it }
        )

        OutlinedTextField(
            value = viewModel.tempoString,
            onValueChange = { viewModel.updateTempo(it) },
            label = { Text("Tempo (BPM)") },
            modifier = Modifier.fillMaxWidth()
        )

        ComboBox(
            label = "Type of Idea",
            options = IdeaType.values().toList(),
            selectedOption = viewModel.ideaType,
            onOptionSelected = { viewModel.ideaType = it }
        )

        OutlinedTextField(
            value = viewModel.customTags,
            onValueChange = { viewModel.customTags = it },
            label = { Text("Custom Tags (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { viewModel.save() },
            // modifier = Modifier.align(LineHeightStyle.Alignment.)
        ) {
            Text("Save")
        }
    }
}