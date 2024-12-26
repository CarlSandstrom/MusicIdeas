package com.musicideas.ui.screens.record

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.audio.recording.AudioRecorder

class RecordViewModel(
    private val audioRecorder: AudioRecorder // Injected dependency
) {
    val recordingDuration = 0
    val isPlaying = false
    var isRecording by mutableStateOf(false)
        private set

    fun startRecording() {
        audioRecorder.startRecording()
        isRecording = true
    }

    fun stopRecording() {
        audioRecorder.stopRecording()
        isRecording = false
    }

    fun getAudioRecorder(): AudioRecorder = audioRecorder
    fun moveCursorBackward() {
        TODO("Not yet implemented")
    }

    fun togglePlayback() {
        TODO("Not yet implemented")
    }

    fun moveCursorForward() {
        TODO("Not yet implemented")
    }
}