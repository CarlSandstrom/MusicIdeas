package com.musicideas.di

import com.musicideas.data.audio.playback.AudioPlayer
import com.musicideas.data.audio.playback.JavaSoundPlayer
import com.musicideas.data.audio.recording.AudioRecorder
import com.musicideas.data.audio.recording.JavaSoundRecorder
import com.musicideas.data.repository.AudioRepositoryImpl
import com.musicideas.data.repository.MusicIdeaRepositoryImpl
import com.musicideas.data.storage.MusicIdeaStorage
import com.musicideas.data.storage.MusicIdeaStorageFileSystem
import com.musicideas.core.repository.AudioRepository
import com.musicideas.core.repository.MusicIdeaRepository
import com.musicideas.presentation.navigation.MainViewModel
import org.koin.dsl.module
import java.io.File

val appModule = module {
    // Data Sources
    single<MusicIdeaStorage> {
        MusicIdeaStorageFileSystem(
            baseDir = File(System.getProperty("user.home"), "MusicIdeas")
        )
    }
    single<AudioRecorder> { JavaSoundRecorder() }
    single<AudioPlayer> { JavaSoundPlayer() }
    single<AudioRepository> { AudioRepositoryImpl(get(), get()) }

    // Repositories
    single<MusicIdeaRepository> { MusicIdeaRepositoryImpl(get()) }

    // ViewModels
    single { MainViewModel(get(), get()) }
}