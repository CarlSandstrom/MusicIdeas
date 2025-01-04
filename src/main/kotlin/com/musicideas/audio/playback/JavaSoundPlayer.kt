package com.musicideas.audio.playback

import com.musicideas.audio.AudioFormatConfig
import kotlinx.coroutines.*
import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine

class JavaSoundPlayer : AudioPlayer {
    private var _isPlaying = false
    override val isPlaying: Boolean get() = _isPlaying

    private var playbackJob: Job? = null
    private var line: SourceDataLine? = null

    override fun play(audioData: ByteArray) {
        if (isPlaying || audioData.isEmpty()) return

        try {
            val audioInputStream = AudioInputStream(
                ByteArrayInputStream(audioData),
                AudioFormatConfig.format,
                audioData.size.toLong() / AudioFormatConfig.format.frameSize
            )

            line = AudioSystem.getSourceDataLine(AudioFormatConfig.format).apply {
                open(AudioFormatConfig.format)
                start()
            }

            _isPlaying = true
            playbackJob = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val buffer = ByteArray(8192)
                    var bytesRead: Int = 0
                    while (isPlaying &&
                        audioInputStream.read(buffer).also { bytesRead = it } != -1) {
                        line?.write(buffer, 0, bytesRead)
                        yield()
                    }
                } finally {
                    cleanup()
                    audioInputStream.close()
                }
            }
        } catch (e: Exception) {
            println("Error during playback: ${e.message}")
            cleanup()
        }
    }

    override fun stop() {
        _isPlaying = false
        cleanup()
    }

    private fun cleanup() {
        _isPlaying = false
        playbackJob?.cancel()
        line?.apply {
            drain()
            stop()
            close()
        }
        line = null
    }
}