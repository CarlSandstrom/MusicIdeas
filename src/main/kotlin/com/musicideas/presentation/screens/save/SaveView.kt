package com.musicideas.presentation.screens.save

import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicideas.core.model.Genre
import com.musicideas.core.model.IdeaType
import com.musicideas.core.model.Instrument
import com.musicideas.presentation.components.ComboBox

@Composable
fun SaveView(viewModel: SaveViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(if (viewModel.isEditing) "Edit Music Idea" else "Save New Music Idea")

        OutlinedTextField(
            value = viewModel.name,
            onValueChange = { viewModel.name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )

        ComboBox(
            label = "Genre",
            options = Genre.entries.toList(),
            selectedOption = viewModel.genre,
            onOptionSelected = { viewModel.genre = it }
        )

        ComboBox(
            label = "Instrument",
            options = Instrument.entries.toList(),
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
            options = IdeaType.entries.toList(),
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
            onClick = { viewModel.save() }
        ) {
            Text(if (viewModel.isEditing) "Update" else "Save")
        }
    }
}