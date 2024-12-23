package com.musicideas.audio.recording

interface AudioRecorder {
    fun startRecording()
    fun stopRecording()
    fun getInputLevel(): Float
}