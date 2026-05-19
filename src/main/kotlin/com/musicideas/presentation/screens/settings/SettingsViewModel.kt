package com.musicideas.presentation.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.core.model.AppSettings
import com.musicideas.core.model.AudioQuality
import com.musicideas.core.model.Genre
import com.musicideas.core.model.IdeaType
import com.musicideas.core.model.Instrument
import com.musicideas.core.repository.AudioRepository
import com.musicideas.core.repository.SettingsRepository
import com.musicideas.presentation.common.ViewModel

class SettingsViewModel(
    private val audioRepository: AudioRepository,
    private val settingsRepository: SettingsRepository,
    private val onDarkModeToggled: (Boolean) -> Unit = {}
) : ViewModel() {

    var audioQuality by mutableStateOf(AudioQuality.HIGH)
    var saveLocation by mutableStateOf(System.getProperty("user.home") + "/MusicIdeas")
    var darkMode by mutableStateOf(false)
    var defaultGenre by mutableStateOf(Genre.ROCK)
    var defaultInstrument by mutableStateOf(Instrument.GUITAR)
    var defaultIdeaType by mutableStateOf(IdeaType.RIFF)
    var showGenreFilter by mutableStateOf(true)
    var showInstrumentFilter by mutableStateOf(true)
    var showIdeaTypeFilter by mutableStateOf(true)
    var showRatingFilter by mutableStateOf(true)

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
        defaultGenre = saved.defaultGenre
        defaultInstrument = saved.defaultInstrument
        defaultIdeaType = saved.defaultIdeaType
        showGenreFilter = saved.showGenreFilter
        showInstrumentFilter = saved.showInstrumentFilter
        showIdeaTypeFilter = saved.showIdeaTypeFilter
        showRatingFilter = saved.showRatingFilter

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
        onDarkModeToggled(enabled)
    }

    fun onDefaultGenreSelected(genre: Genre) {
        defaultGenre = genre
        saveSettings()
    }

    fun onDefaultInstrumentSelected(instrument: Instrument) {
        defaultInstrument = instrument
        saveSettings()
    }

    fun onDefaultIdeaTypeSelected(ideaType: IdeaType) {
        defaultIdeaType = ideaType
        saveSettings()
    }

    fun onShowGenreFilterChanged(show: Boolean) { showGenreFilter = show; saveSettings() }
    fun onShowInstrumentFilterChanged(show: Boolean) { showInstrumentFilter = show; saveSettings() }
    fun onShowIdeaTypeFilterChanged(show: Boolean) { showIdeaTypeFilter = show; saveSettings() }
    fun onShowRatingFilterChanged(show: Boolean) { showRatingFilter = show; saveSettings() }

    private fun saveSettings() {
        settingsRepository.save(
            AppSettings(
                selectedInputDevice = selectedInputDevice,
                audioQuality = audioQuality,
                saveLocation = saveLocation,
                darkMode = darkMode,
                defaultGenre = defaultGenre,
                defaultInstrument = defaultInstrument,
                defaultIdeaType = defaultIdeaType,
                showGenreFilter = showGenreFilter,
                showInstrumentFilter = showInstrumentFilter,
                showIdeaTypeFilter = showIdeaTypeFilter,
                showRatingFilter = showRatingFilter
            )
        )
    }
}
