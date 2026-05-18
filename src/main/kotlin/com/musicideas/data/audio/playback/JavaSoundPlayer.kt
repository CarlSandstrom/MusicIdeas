package com.musicideas.data.audio.playback

import kotlinx.coroutines.yield
import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.SourceDataLine

class JavaSoundPlayer : AudioPlayer {
    private var _isPlaying = false
    override val isPlaying: Boolean get() = _isPlaying

    private var line: SourceDataLine? = null

    override suspend fun play(audioData: ByteArray, sampleRate: Int) {
        if (isPlaying || audioData.isEmpty()) return

        val format = javax.sound.sampled.AudioFormat(sampleRate.toFloat(), 16, 1, true, false)
        val audioInputStream = AudioInputStream(
            ByteArrayInputStream(audioData),
            format,
            audioData.size.toLong() / format.frameSize
        )

        try {
            line = AudioSystem.getSourceDataLine(format).apply {
                open(format)
                start()
            }
            _isPlaying = true

            val buffer = ByteArray(8192)
            var bytesRead = 0
            while (isPlaying && audioInputStream.read(buffer).also { bytesRead = it } != -1) {
                line?.write(buffer, 0, bytesRead)
                yield()
            }
        } catch (e: Exception) {
            println("Error during playback: ${e.message}")
        } finally {
            cleanup()
            audioInputStream.close()
        }
    }

    override fun stop() {
        cleanup()
    }

    private fun cleanup() {
        _isPlaying = false
        line?.apply {
            drain()
            stop()
            close()
        }
        line = null
    }
}