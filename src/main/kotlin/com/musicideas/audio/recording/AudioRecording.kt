package com.musicideas.audio.recording

interface AudioRecorder {
    fun startRecording()
    fun stopRecording(): ByteArray
    fun getInputLevel(): Float
    val isRecording: Boolean
}