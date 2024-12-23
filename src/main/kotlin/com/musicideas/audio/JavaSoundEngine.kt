package com.musicideas.audio

import kotlinx.coroutines.*
import javax.sound.sampled.*
import kotlin.math.PI
import kotlin.math.sin

class JavaSoundEngine : AudioEngine {
    private val engineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val playbackMixer = AudioSystem.getMixer(null)

    override fun playSound(frequency: Float, durationMs: Int) {
        engineScope.launch {
            val audioData = generateSineWave(frequency, durationMs)
            playAudioData(audioData)
        }
    }

    private fun generateSineWave(frequency: Float, durationMs: Int): ByteArray {
        val sampleRate = 44100f
        val numSamples = (durationMs * sampleRate / 1000).toInt()
        val audioData = ByteArray(2 * numSamples)

        for (i in 0 until numSamples) {
            val time = i.toFloat() / sampleRate
            val amplitude = (Short.MAX_VALUE * sin(2.0 * PI * frequency * time)).toInt()

            audioData[2*i] = (amplitude and 0xFF).toByte()
            audioData[2*i + 1] = (amplitude shr 8).toByte()
        }

        return audioData
    }

    private fun playAudioData(audioData: ByteArray) {
        val format = AudioFormat(44100f, 16, 1, true, true)
        AudioSystem.getSourceDataLine(format).use { line ->
            line.open(format)
            line.start()
            line.write(audioData, 0, audioData.size)
            line.drain()
        }
    }
}
