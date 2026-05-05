package com.musicideas.presentation.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.core.model.AppSettings
import com.musicideas.core.model.AudioQuality
import com.musicideas.core.repository.AudioRepository
import com.musicideas.core.repository.SettingsRepository
import com.musicideas.presentation.common.ViewModel

class SettingsViewModel(
    private val audioRepository: AudioRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    var audioQuality by mutableStateOf(AudioQuality.HIGH)
    var saveLocation by mutableStateOf(System.getProperty("user.home") + "/MusicIdeas")
    var darkMode by mutableStateOf(false)

    var availableInputDevices by mutableStateOf(emptyList<String>())
        private set
    var selectedInputDevice by mutableStateOf("")
        private set

    init {
        availableInputDevices = audioRepository.availableInputDevices()

        val saved = settingsRepository.read()
        audioQuality = saved.audioQuality
        saveLocation = saved.saveLocation
        darkMode = saved.darkMode

        selectedInputDevice = if (saved.selectedInputDevice.isNotEmpty() &&
            availableInputDevices.contains(saved.selectedInputDevice)
        ) {
            saved.selectedInputDevice
        } else {
            availableInputDevices.firstOrNull() ?: ""
        }

        if (selectedInputDevice.isNotEmpty()) audioRepository.setInputDevice(selectedInputDevice)
    }

    fun onInputDeviceSelected(name: String) {
        selectedInputDevice = name
        audioRepository.setInputDevice(name)
        saveSettings()
    }

    fun onAudioQualitySelected(quality: AudioQuality) {
        audioQuality = quality
        saveSettings()
    }

    fun onSaveLocationChanged(location: String) {
        saveLocation = location
        saveSettings()
    }

    fun onDarkModeChanged(enabled: Boolean) {
        darkMode = enabled
        saveSettings()
    }

    private fun saveSettings() {
        settingsRepository.save(
            AppSettings(
                selectedInputDevice = selectedInputDevice,
                audioQuality = audioQuality,
                saveLocation = saveLocation,
                darkMode = darkMode
            )
        )
    }
}
