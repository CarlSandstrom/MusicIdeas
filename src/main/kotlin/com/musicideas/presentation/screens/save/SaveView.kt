package com.musicideas.presentation.screens.save

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import com.musicideas.core.model.Genre
import com.musicideas.core.model.IdeaType
import com.musicideas.core.model.Instrument
import com.musicideas.presentation.components.ComboBox

@Composable
fun SaveView(viewModel: SaveViewModel) {
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier.fillMaxSize().onPreviewKeyEvent { keyEvent ->
            if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyDown) {
                viewModel.save()
                true
            } else false
        }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(16.dp),
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

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Rating: ", style = MaterialTheme.typography.bodyMedium)
            for (i in 1..5) {
                IconButton(onClick = { viewModel.rating = if (viewModel.rating == i) 0 else i }) {
                    Icon(
                        imageVector = if (i <= viewModel.rating) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "$i stars",
                        tint = if (i <= viewModel.rating) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        viewModel.saveError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = { viewModel.save() }
        ) {
            Text(if (viewModel.isEditing) "Update" else "Save")
        }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}