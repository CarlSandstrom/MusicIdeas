package com.musicideas.presentation.navigation

import com.musicideas.core.model.MusicIdea
import com.musicideas.core.repository.AudioRepository
import com.musicideas.core.repository.ExportDialog
import com.musicideas.core.repository.MusicIdeaRepository
import com.musicideas.core.repository.SettingsRepository
import com.musicideas.presentation.common.ViewModel
import com.musicideas.presentation.screens.cloudstorage.CloudStorageViewModel
import com.musicideas.presentation.screens.library.LibraryViewModel
import com.musicideas.presentation.screens.record.RecordViewModel
import com.musicideas.presentation.screens.save.SaveViewModel
import com.musicideas.presentation.screens.settings.SettingsViewModel

class MainViewModel(
    private val musicIdeaRepository: MusicIdeaRepository,
    private val audioRepository: AudioRepository,
    private val exportDialog: ExportDialog,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    fun createRecordViewModel(): RecordViewModel {
        return RecordViewModel(audioRepository)
    }

    fun createLibraryViewModel(): LibraryViewModel {
        return LibraryViewModel(musicIdeaRepository, audioRepository, exportDialog)
    }

    fun createCloudStorageViewModel(): CloudStorageViewModel {
        return CloudStorageViewModel()
    }

    fun createSettingsViewModel(): SettingsViewModel {
        return SettingsViewModel(audioRepository, settingsRepository)
    }

    fun createSaveViewModel(
        audioData: ByteArray,
        initialTempo: Int = 120,
        existingMusicIdea: MusicIdea? = null,
        onSaveComplete: () -> Unit
    ): SaveViewModel {
        val settings = settingsRepository.read()
        return SaveViewModel(
            audioData = audioData,
            repository = musicIdeaRepository,
            initialTempo = initialTempo,
            existingMusicIdea = existingMusicIdea,
            onSaveComplete = onSaveComplete,
            defaultGenre = settings.defaultGenre,
            defaultInstrument = settings.defaultInstrument
        )
    }
}