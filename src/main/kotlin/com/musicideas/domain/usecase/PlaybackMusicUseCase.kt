package com.musicideas.domain.usecase

import com.musicideas.domain.repository.AudioRepository

class PlaybackMusicUseCase(
    private val audioRepository: AudioRepository
) {
    suspend fun startPlayback(audioData: ByteArray): Result<Unit> =
        audioRepository.playAudio(audioData)

    suspend fun stopPlayback(): Result<Unit> =
        audioRepository.stopPlayback()
}