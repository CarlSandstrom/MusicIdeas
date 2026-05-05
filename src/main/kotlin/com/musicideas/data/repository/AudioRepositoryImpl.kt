package com.musicideas.data.repository

import com.musicideas.data.audio.playback.AudioPlayer
import com.musicideas.data.audio.recording.AudioRecorder
import com.musicideas.core.repository.AudioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioRepositoryImpl(
    private val recorder: AudioRecorder,
    private val player: AudioPlayer
) : AudioRepository {
    override suspend fun startRecording(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            println("Starting recording")
            recorder.startRecording()
        }
    }

    override suspend fun stopRecording(): Result<ByteArray> = withContext(Dispatchers.IO) {
        runCatching {
            println("Stopping recording")
            recorder.stopRecording()
        }
    }

    override suspend fun playAudio(audioData: ByteArray): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            println("Playing audio: ${audioData.size} bytes")
            player.play(audioData)
        }
    }

    override suspend fun stopPlayback(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            println("Stopping playback")
            player.stop()
        }
    }

    override fun getInputLevel(): Float = recorder.getInputLevel()

    override fun availableInputDevices(): List<String> =
        recorder.availableInputDevices().map { it.name }

    override fun setInputDevice(name: String) {
        val mixerInfo = recorder.availableInputDevices().firstOrNull { it.name == name }
        recorder.setInputDevice(mixerInfo)
    }

}
