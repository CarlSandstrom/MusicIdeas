package com.musicideas.domain.usecase

import com.musicideas.domain.repository.AudioRepository

class RecordMusicUseCase(
    private val audioRepository: AudioRepository
) {
    // Changed to only start recording
    suspend fun startRecording(): Result<Unit> =
        audioRepository.startRecording()

    // Added separate stop method
    suspend fun stopRecording(): Result<ByteArray> =
        audioRepository.stopRecording()
}