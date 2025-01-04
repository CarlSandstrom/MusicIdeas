package com.musicideas.di

import com.musicideas.audio.playback.AudioPlayer
import com.musicideas.audio.playback.JavaSoundPlayer
import com.musicideas.audio.recording.AudioRecorder
import com.musicideas.audio.recording.JavaSoundRecorder
import com.musicideas.data.repository.AudioRepositoryImpl
import com.musicideas.data.repository.MusicIdeaRepositoryImpl
import com.musicideas.data.source.local.MusicIdeaLocalDataSource
import com.musicideas.data.source.local.MusicIdeaLocalDataSourceImpl
import com.musicideas.domain.repository.AudioRepository
import com.musicideas.domain.repository.MusicIdeaRepository
import com.musicideas.domain.usecase.GetMusicIdeaUseCase
import com.musicideas.domain.usecase.PlaybackMusicUseCase
import com.musicideas.domain.usecase.RecordMusicUseCase
import com.musicideas.domain.usecase.SaveMusicIdeaUseCase
import com.musicideas.presentation.navigation.MainViewModel
import org.koin.dsl.module
import java.io.File

val appModule = module {
    // Data Sources
    single<MusicIdeaLocalDataSource> {
        MusicIdeaLocalDataSourceImpl(
            baseDir = File(System.getProperty("user.home"), "MusicIdeas")
        )
    }
    single<AudioRecorder> { JavaSoundRecorder() }
    single<AudioPlayer> { JavaSoundPlayer() }
    single<AudioRepository> { AudioRepositoryImpl(get(), get()) }

    // Repositories
    single<MusicIdeaRepository> { MusicIdeaRepositoryImpl(get()) }

    // Use Cases
    single { RecordMusicUseCase(get()) }
    single { PlaybackMusicUseCase(get()) }
    single { SaveMusicIdeaUseCase(get()) }
    single { GetMusicIdeaUseCase(get()) }

    // ViewModels
    single { MainViewModel(get(), get(), get(), get(), get()) }
}