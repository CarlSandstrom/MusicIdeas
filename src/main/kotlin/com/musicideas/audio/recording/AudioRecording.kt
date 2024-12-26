package com.musicideas.audio.recording

interface AudioRecorder {
    fun startRecording()
    fun stopRecording()
    fun startPlayback()
    fun stopPlayback()
    fun getInputLevel(): Float
}