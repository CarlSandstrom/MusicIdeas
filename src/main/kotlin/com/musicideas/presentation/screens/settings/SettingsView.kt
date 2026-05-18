package com.musicideas.presentation.screens.settings

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicideas.core.model.AudioQuality
import com.musicideas.core.model.Genre
import com.musicideas.core.model.Instrument
import com.musicideas.presentation.components.ComboBox

@Composable
fun SettingsView(viewModel: SettingsViewModel) {
    val scrollState = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ComboBox(
                label = "Input Device",
                options = viewModel.availableInputDevices,
                selectedOption = viewModel.selectedInputDevice,
                onOptionSelected = { viewModel.onInputDeviceSelected(it) }
            )

            ComboBox(
                label = "Audio Quality",
                options = AudioQuality.entries.toList(),
                selectedOption = viewModel.audioQuality,
                onOptionSelected = { viewModel.onAudioQualitySelected(it) }
            )

            TextField(
                value = viewModel.saveLocation,
                onValueChange = { viewModel.onSaveLocationChanged(it) },
                label = { Text("Save Location") },
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                "Restart required for storage location changes to take effect.",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )

            ComboBox(
                label = "Default Genre",
                options = Genre.entries.toList(),
                selectedOption = viewModel.defaultGenre,
                onOptionSelected = { viewModel.onDefaultGenreSelected(it) }
            )

            ComboBox(
                label = "Default Instrument",
                options = Instrument.entries.toList(),
                selectedOption = viewModel.defaultInstrument,
                onOptionSelected = { viewModel.onDefaultInstrumentSelected(it) }
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Dark Mode")
                Switch(
                    checked = viewModel.darkMode,
                    onCheckedChange = { viewModel.onDarkModeChanged(it) }
                )
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}
