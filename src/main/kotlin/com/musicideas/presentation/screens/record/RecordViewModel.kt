package com.musicideas.presentation.screens.record

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.domain.repository.AudioRepository
import com.musicideas.domain.usecase.PlaybackMusicUseCase
import com.musicideas.domain.usecase.RecordMusicUseCase
import com.musicideas.presentation.common.ViewModel
import kotlinx.coroutines.launch


class RecordViewModel(
    private val recordMusicUseCase: RecordMusicUseCase,
    private val playbackMusicUseCase: PlaybackMusicUseCase,
    private val audioRepository: AudioRepository
) : ViewModel() {
    var isRecording by mutableStateOf(false)
        private set

    var currentTimeMs by mutableStateOf(0f)
        private set

    var audioData by mutableStateOf<ByteArray?>(null)
        private set

    fun startRecording() {
        viewModelScope.launch {
            isRecording = true
            recordMusicUseCase().onSuccess { audio ->
                audioData = audio
            }
        }
    }

    fun startPlayback() {
        viewModelScope.launch {
            audioData?.let { audio ->
                playbackMusicUseCase.startPlayback(audio)
            }
        }
    }

    fun getInputLevel() = audioRepository.getInputLevel()

    fun stopRecording() {
        isRecording = false
        viewModelScope.launch {
            audioRepository.stopRecording().onSuccess { audio ->
                audioData = audio
            }
        }
    }
}