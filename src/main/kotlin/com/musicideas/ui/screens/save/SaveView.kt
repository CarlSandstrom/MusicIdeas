package com.musicideas.ui.screens.save

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.musicideas.models.*
import com.musicideas.ui.components.ComboBox

@Composable
fun SaveView(viewModel: SaveViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Genre dropdown
        ComboBox(
            label = "Genre",
            options = Genre.values().toList(),
            selectedOption = viewModel.genre,
            onOptionSelected = { viewModel.genre = it }
        )

        // Instrument dropdown
        ComboBox(
            label = "Instrument",
            options = Instrument.values().toList(),
            selectedOption = viewModel.instrument,
            onOptionSelected = { viewModel.instrument = it }
        )

        // Tempo input field
        OutlinedTextField(
            value = viewModel.tempoString,
            onValueChange = { viewModel.updateTempo(it) },
            label = { Text("Tempo (BPM)") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Idea Type dropdown
        ComboBox(
            label = "Type of Idea",
            options = IdeaType.values().toList(),
            selectedOption = viewModel.ideaType,
            onOptionSelected = { viewModel.ideaType = it }
        )

        // Custom tags
        OutlinedTextField(
            value = viewModel.customTags,
            onValueChange = { viewModel.customTags = it },
            label = { Text("Custom Tags (comma-separated)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Save button
        Button(
            onClick = { viewModel.save() },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}