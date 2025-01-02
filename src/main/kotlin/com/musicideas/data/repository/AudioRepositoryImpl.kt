package com.musicideas.data.repository

import com.musicideas.domain.repository.AudioRepository
import com.musicideas.audio.recording.AudioRecorder
import com.musicideas.audio.recording.JavaSoundRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioRepositoryImpl(
    private val audioRecorder: AudioRecorder
) : AudioRepository {
    override suspend fun startRecording(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            audioRecorder.startRecording()
        }
    }

    override suspend fun stopRecording(): Result<ByteArray> = withContext(Dispatchers.IO) {
        runCatching {
            audioRecorder.stopRecording()
            (audioRecorder as? JavaSoundRecorder)?.getRecordedAudio() ?: ByteArray(0)
        }
    }

    override suspend fun playAudio(audioData: ByteArray): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            audioRecorder.startPlayback()
        }
    }

    override suspend fun stopPlayback(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            audioRecorder.stopPlayback()
        }
    }

    override fun getInputLevel(): Float = audioRecorder.getInputLevel()
}