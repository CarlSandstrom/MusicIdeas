package com.musicideas.domain.usecase

import com.musicideas.domain.repository.AudioRepository

class RecordMusicUseCase(
    private val audioRepository: AudioRepository
) {
    suspend operator fun invoke(): Result<ByteArray> =
        try {
            audioRepository.startRecording().getOrThrow()
            audioRepository.stopRecording()
        } catch (e: Exception) {
            Result.failure(e)
        }
}