package com.musicideas.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.musicideas.presentation.components.ComboBox

@Composable
fun SettingsView(viewModel: SettingsViewModel) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
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
            onOptionSelected = { viewModel.audioQuality = it }
        )

        TextField(
            value = viewModel.saveLocation,
            onValueChange = { viewModel.saveLocation = it },
            label = { Text("Save Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Dark Mode")
            Switch(
                checked = viewModel.darkMode,
                onCheckedChange = { viewModel.darkMode = it }
            )
        }
    }
}