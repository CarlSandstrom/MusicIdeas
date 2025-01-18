package com.musicideas.presentation.navigation

import com.musicideas.domain.model.MusicIdea
import com.musicideas.domain.repository.AudioRepository
import com.musicideas.domain.repository.MusicIdeaRepository
import com.musicideas.presentation.common.ViewModel
import com.musicideas.presentation.screens.cloudstorage.CloudStorageViewModel
import com.musicideas.presentation.screens.library.LibraryViewModel
import com.musicideas.presentation.screens.record.RecordViewModel
import com.musicideas.presentation.screens.save.SaveViewModel
import com.musicideas.presentation.screens.settings.SettingsViewModel

class MainViewModel(
    private val musicIdeaRepository: MusicIdeaRepository,
    private val audioRepository: AudioRepository
) : ViewModel() {

    fun createRecordViewModel(): RecordViewModel {
        return RecordViewModel(audioRepository)
    }

    fun createLibraryViewModel(): LibraryViewModel {
        return LibraryViewModel(musicIdeaRepository, audioRepository)
    }

    fun createCloudStorageViewModel(): CloudStorageViewModel {
        return CloudStorageViewModel()
    }

    fun createSettingsViewModel(): SettingsViewModel {
        return SettingsViewModel()
    }

    fun createSaveViewModel(
        audioData: ByteArray,
        existingMusicIdea: MusicIdea? = null,
        onSaveComplete: () -> Unit
    ): SaveViewModel {
        return SaveViewModel(
            audioData = audioData,
            repository = musicIdeaRepository,
            existingMusicIdea = existingMusicIdea,
            onSaveComplete = onSaveComplete
        )
    }
}