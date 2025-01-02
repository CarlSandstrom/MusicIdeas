package com.musicideas.presentation.screens.settings

import com.musicideas.presentation.common.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SettingsViewModel : ViewModel() {
    var audioQuality by mutableStateOf(AudioQuality.HIGH)
    var saveLocation by mutableStateOf(System.getProperty("user.home") + "/MusicIdeas")
    var darkMode by mutableStateOf(false)
}

enum class AudioQuality {
    LOW, MEDIUM, HIGH
}