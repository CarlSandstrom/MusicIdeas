package com.musicideas.presentation.navigation

import com.musicideas.domain.repository.AudioRepository
import com.musicideas.domain.usecase.GetMusicIdeaUseCase
import com.musicideas.domain.usecase.PlaybackMusicUseCase
import com.musicideas.domain.usecase.RecordMusicUseCase
import com.musicideas.domain.usecase.SaveMusicIdeaUseCase
import com.musicideas.presentation.common.ViewModel
import com.musicideas.presentation.screens.cloudstorage.CloudStorageViewModel
import com.musicideas.presentation.screens.library.LibraryViewModel
import com.musicideas.presentation.screens.record.RecordViewModel
import com.musicideas.presentation.screens.save.SaveViewModel
import com.musicideas.presentation.screens.settings.SettingsViewModel

class MainViewModel(
    private val recordMusicUseCase: RecordMusicUseCase,
    private val playbackMusicUseCase: PlaybackMusicUseCase,
    private val saveMusicIdeaUseCase: SaveMusicIdeaUseCase,
    private val getMusicIdeaUseCase: GetMusicIdeaUseCase,
    private val audioRepository: AudioRepository
) : ViewModel() {

    fun createRecordViewModel(): RecordViewModel {
        return RecordViewModel(recordMusicUseCase, playbackMusicUseCase, audioRepository)
    }

    fun createLibraryViewModel(): LibraryViewModel {
        return LibraryViewModel(getMusicIdeaUseCase)
    }

    fun createCloudStorageViewModel(): CloudStorageViewModel {
        return CloudStorageViewModel()
    }

    fun createSettingsViewModel(): SettingsViewModel {
        return SettingsViewModel()
    }

    fun createSaveViewModel(audioData: ByteArray, onSaveComplete: () -> Unit): SaveViewModel {
        return SaveViewModel(audioData, saveMusicIdeaUseCase, onSaveComplete)
    }
}