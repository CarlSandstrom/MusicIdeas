package com.musicideas.core.repository

import kotlinx.coroutines.flow.Flow

interface AudioRepository {
    fun startMonitoring()
    fun stopMonitoring()
    suspend fun startRecording(): Result<Unit>
    suspend fun stopRecording(): Result<ByteArray>
    suspend fun playAudio(audioData: ByteArray): Result<Unit>
    suspend fun stopPlayback(): Result<Unit>
    val recordingChunks: Flow<ByteArray>
    fun getInputLevel(): Float
    fun availableInputDevices(): List<String>
    fun setInputDevice(name: String)
}