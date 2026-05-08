package com.musicideas.presentation.screens.record

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.core.repository.AudioRepository
import com.musicideas.presentation.common.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sqrt

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

    var playbackLevel by mutableStateOf(0f)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        audioRepository.startMonitoring()
    }

    fun clearError() { errorMessage = null }

    fun startRecording() {
        errorMessage = null
        viewModelScope.launch {
            audioRepository.startRecording()
                .onSuccess { isRecording = true }
                .onFailure { errorMessage = "Failed to start recording: ${it.message}" }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            audioRepository.stopRecording()
                .onSuccess { audio ->
                    audioData = audio
                    isRecording = false
                    audioRepository.startMonitoring()
                }
                .onFailure { errorMessage = "Failed to stop recording: ${it.message}" }
        }
    }

    fun startPlayback() {
        viewModelScope.launch {
            audioData?.let { audio ->
                isPlaying = true
                currentTimeMs = 0f
                val startTime = System.currentTimeMillis()
                val positionJob = launch {
                    while (isPlaying) {
                        currentTimeMs = (System.currentTimeMillis() - startTime).toFloat()
                        playbackLevel = rmsLevelAt(audio, currentTimeMs)
                        delay(50)
                    }
                }
                audioRepository.playAudio(audio)
                positionJob.cancel()
                isPlaying = false
                currentTimeMs = 0f
                playbackLevel = 0f
            }
        }
    }

    fun stopPlayback() {
        viewModelScope.launch {
            audioRepository.stopPlayback()
            isPlaying = false
            currentTimeMs = 0f
        }
    }

    fun getInputLevel(): Float = audioRepository.getInputLevel()

    fun getLevel(): Float = if (isPlaying) playbackLevel else getInputLevel()

    private fun rmsLevelAt(audio: ByteArray, positionMs: Float): Float {
        val byteOffset = (positionMs / 1000f * 44100f * 2).toInt().and(1.inv()).coerceIn(0, audio.size)
        val end = (byteOffset + 4096).coerceAtMost(audio.size).and(1.inv())
        if (end <= byteOffset) return 0f
        var sum = 0.0
        var i = byteOffset
        while (i + 1 < end) {
            val sample = (audio[i + 1].toInt() shl 8) or (audio[i].toInt() and 0xFF)
            sum += sample.toDouble() * sample.toDouble()
            i += 2
        }
        val count = (end - byteOffset) / 2
        return if (count > 0) sqrt(sum / count).toFloat() / 32768f else 0f
    }

    override fun onCleared() {
        audioRepository.stopMonitoring()
        super.onCleared()
    }
}
