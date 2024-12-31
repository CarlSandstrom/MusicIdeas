// MainViewModel.kt
package com.musicideas.ui.navigation

import com.musicideas.AppContainer
import com.musicideas.ui.screens.cloudstorage.CloudStorageViewModel
import com.musicideas.ui.screens.library.LibraryViewModel
import com.musicideas.ui.screens.record.RecordViewModel
import com.musicideas.ui.screens.settings.SettingsViewModel
import com.musicideas.ui.screens.save.SaveViewModel

class MainViewModel(private val appContainer: AppContainer) {
    fun createRecordViewModel(): RecordViewModel {
        return RecordViewModel(appContainer.audioRecorder)
    }

    fun createLibraryViewModel(): LibraryViewModel {
        return LibraryViewModel()
    }

    fun createSettingsViewModel(): SettingsViewModel {
        return SettingsViewModel()
    }

    fun createCloudStorageViewModel(): CloudStorageViewModel {
        return CloudStorageViewModel()
    }

    fun createSaveViewModel(audioData: ByteArray, onSaveComplete: () -> Unit): SaveViewModel {
        return SaveViewModel(audioData) {}
    }
}