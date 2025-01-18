package com.musicideas.domain.repository

interface AudioRepository {
    suspend fun startRecording(): Result<Unit>
    suspend fun stopRecording(): Result<ByteArray>
    suspend fun playAudio(audioData: ByteArray): Result<Unit>
    suspend fun stopPlayback(): Result<Unit>

    fun getInputLevel(): Float
}