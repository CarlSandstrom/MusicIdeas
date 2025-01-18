package com.musicideas.presentation.screens.record

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.domain.repository.AudioRepository
import com.musicideas.presentation.common.ViewModel
import kotlinx.coroutines.launch

class RecordViewModel(
    private val audioRepository: AudioRepository
) : ViewModel() {
    var isRecording by mutableStateOf(false)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var currentTimeMs by mutableStateOf(0f)
        private set

    var audioData by mutableStateOf<ByteArray?>(null)
        private set

    fun startRecording() {
        viewModelScope.launch {
            audioRepository.startRecording().onSuccess {
                isRecording = true
            }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            audioRepository.stopRecording().onSuccess { audio ->
                audioData = audio
                isRecording = false
            }
        }
    }

    fun startPlayback() {
        viewModelScope.launch {
            audioData?.let { audio ->
                audioRepository.playAudio(audio)
                isPlaying = true
            }
        }
    }

    fun stopPlayback() {
        viewModelScope.launch {
            audioRepository.stopPlayback()
            isPlaying = false
        }
    }

    fun getInputLevel(): Float {
        return audioRepository.getInputLevel()
    }
}