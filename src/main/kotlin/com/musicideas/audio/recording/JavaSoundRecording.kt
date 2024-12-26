package com.musicideas.audio.recording

import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import javax.sound.sampled.*
import kotlin.math.sqrt

class JavaSoundRecorder : AudioRecorder {
    private var recording = false
    private var line: TargetDataLine? = null
    private val bufferSize = 4096
    private val buffer = ByteArray(bufferSize)
    private var recordingJob: Job? = null
    private var currentLevel = 0f

    // Dynamic buffer to store the recorded audio
    private val audioBuffer = ByteArrayOutputStream()

    // Audio format for recording
    private val audioFormat = AudioFormat(44100f, 16, 1, true, true)

    override fun startRecording() {
        if (recording) return

        // Clear previous recording
        audioBuffer.reset()

        line = AudioSystem.getTargetDataLine(audioFormat).apply {
            open(audioFormat, bufferSize)
            start()
        }

        recording = true
        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            while (recording) {
                val count = line?.read(buffer, 0, bufferSize) ?: 0
                if (count > 0) {
                    // Store the recorded data
                    audioBuffer.write(buffer, 0, count)
                    // Update the current input level
                    currentLevel = calculateRMSLevel(buffer, count)
                }
                delay(50) // Update level every 50ms
            }
        }
    }

    override fun stopRecording() {
        recording = false
        recordingJob?.cancel()
        line?.stop()
        line?.close()
        line = null
    }

    override fun startPlayback() {
        // Create an audio input stream from the recorded data
        val audioData = audioBuffer.toByteArray()
        val audioInputStream = AudioInputStream(
            audioData.inputStream(),
            audioFormat,
            audioData.size.toLong() / audioFormat.frameSize
        )

        // Get a source data line for playback
        val dataLine = AudioSystem.getSourceDataLine(audioFormat).apply {
            open(audioFormat)
            start()
        }

        // Play the audio in a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val playbackBuffer = ByteArray(bufferSize)
                var bytesRead = 0
                while (bytesRead != -1) {
                    bytesRead = audioInputStream.read(playbackBuffer, 0, playbackBuffer.size)
                    if (bytesRead >= 0) {
                        dataLine.write(playbackBuffer, 0, bytesRead)
                    }
                }
            } finally {
                dataLine.drain()
                dataLine.stop()
                dataLine.close()
                audioInputStream.close()
            }
        }
    }

    override fun stopPlayback() {
        // Implementation would depend on how you want to handle playback interruption
        // You might want to add a flag to stop the playback coroutine
    }

    override fun getInputLevel(): Float {
        if (recording) {
            return currentLevel
        } else {
            return 0f
        }
    }

    private fun calculateRMSLevel(buffer: ByteArray, count: Int): Float {
        var sum = 0.0
        for (i in 0 until count step 2) {
            val sample = (buffer[i + 1].toInt() shl 8) or (buffer[i].toInt() and 0xFF)
            sum += (sample * sample).toDouble()
        }
        return sqrt(sum / (count / 2)).toFloat() / 32768f
    }

    // New method to get the recorded audio as a byte array
    fun getRecordedAudio(): ByteArray = audioBuffer.toByteArray()

    // New method to get the current duration in seconds
    fun getCurrentDuration(): Double {
        val bytes = audioBuffer.size()
        val frameSize = audioFormat.frameSize
        val frameRate = audioFormat.frameRate
        return bytes.toDouble() / (frameSize * frameRate)
    }
}