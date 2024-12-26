package com.musicideas.ui.screens.record

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.audio.recording.AudioRecorder
import com.musicideas.audio.recording.JavaSoundRecorder
import kotlinx.coroutines.*

class RecordViewModel(
    private val audioRecorder: AudioRecorder
) {
    var isRecording by mutableStateOf(false)
        private set

    var currentTimeMs by mutableStateOf(0f)
        private set

    var audioData by mutableStateOf<ByteArray?>(null)
        private set

    private var playbackJob: Job? = null

    init {
        // Update current time periodically
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                if (isRecording) {
                    currentTimeMs = ((audioRecorder as JavaSoundRecorder).getCurrentDuration() * 1000).toFloat()
                }
                delay(50) // Update every 50ms
            }
        }
    }

    fun startRecording() {
        audioRecorder.startRecording()
        isRecording = true
        currentTimeMs = 0f
    }

    fun stopRecording() {
        audioRecorder.stopRecording()
        isRecording = false
        audioData = (audioRecorder as JavaSoundRecorder).getRecordedAudio()
    }

    fun startPlayback() {
        audioRecorder.startPlayback()
        playbackJob = CoroutineScope(Dispatchers.Main).launch {
            val startTime = System.currentTimeMillis()
            while (isActive) {
                currentTimeMs = System.currentTimeMillis() - startTime.toFloat()
                delay(50)
            }
        }
    }

    fun stopPlayback() {
        audioRecorder.stopPlayback()
        playbackJob?.cancel()
        playbackJob = null
    }

    fun getAudioRecorder(): AudioRecorder = audioRecorder
}