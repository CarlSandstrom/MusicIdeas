package com.musicideas.di

import com.musicideas.core.repository.AudioRepository
import com.musicideas.core.repository.ExportDialog
import com.musicideas.core.repository.MusicIdeaRepository
import com.musicideas.core.repository.SettingsRepository
import com.musicideas.data.settings.LinuxSettingsRepository
import com.musicideas.data.settings.WindowsSettingsRepository
import com.musicideas.data.audio.playback.AudioPlayer
import com.musicideas.data.audio.playback.JavaSoundPlayer
import com.musicideas.data.audio.recording.AudioRecorder
import com.musicideas.data.audio.recording.JavaSoundRecorder
import com.musicideas.data.audio.encoding.Mp3Decoder
import com.musicideas.data.audio.encoding.Mp3Encoder
import com.musicideas.data.platform.ExportDialogDesktop
import com.musicideas.data.repository.AudioRepositoryImpl
import com.musicideas.data.repository.MusicIdeaRepositoryImpl
import com.musicideas.data.storage.MusicIdeaStorage
import com.musicideas.data.storage.MusicIdeaStorageFileSystem
import com.musicideas.presentation.navigation.MainViewModel
import org.koin.dsl.module
import java.io.File

val appModule = module {
    // Settings
    single<SettingsRepository> {
        if (System.getProperty("os.name").lowercase().contains("win"))
            WindowsSettingsRepository()
        else
            LinuxSettingsRepository()
    }

    // Data Sources
    single { Mp3Encoder() }
    single { Mp3Decoder() }
    single<MusicIdeaStorage> {
        MusicIdeaStorageFileSystem(
            baseDir = File(get<SettingsRepository>().read().saveLocation),
            mp3Encoder = get(),
            mp3Decoder = get()
        )
    }
    single<AudioRecorder> { JavaSoundRecorder() }
    single<AudioPlayer> { JavaSoundPlayer() }
    single<AudioRepository> { AudioRepositoryImpl(get(), get()) }

    // Repositories
    single<MusicIdeaRepository> { MusicIdeaRepositoryImpl(get(), get()) }

    // Dialogs
    single<ExportDialog> { ExportDialogDesktop() }

    // ViewModels
    single { MainViewModel(get(), get(), get(), get()) }
}