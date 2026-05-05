package com.musicideas.presentation.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.core.repository.AudioRepository
import com.musicideas.presentation.common.ViewModel

class SettingsViewModel(private val audioRepository: AudioRepository) : ViewModel() {
    var audioQuality by mutableStateOf(AudioQuality.HIGH)
    var saveLocation by mutableStateOf(System.getProperty("user.home") + "/MusicIdeas")
    var darkMode by mutableStateOf(false)

    var availableInputDevices by mutableStateOf(emptyList<String>())
        private set
    var selectedInputDevice by mutableStateOf("")
        private set

    init {
        availableInputDevices = audioRepository.availableInputDevices()
        selectedInputDevice = availableInputDevices.firstOrNull() ?: ""
        if (selectedInputDevice.isNotEmpty()) audioRepository.setInputDevice(selectedInputDevice)
    }

    fun onInputDeviceSelected(name: String) {
        selectedInputDevice = name
        audioRepository.setInputDevice(name)
    }
}

enum class AudioQuality {
    LOW, MEDIUM, HIGH
}