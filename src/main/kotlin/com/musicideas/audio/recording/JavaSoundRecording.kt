package com.musicideas.audio.recording

import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.util.concurrent.ConcurrentLinkedQueue
import javax.sound.sampled.*
import kotlin.math.sqrt

open class JavaSoundRecorder : AudioRecorder {
    private var recording = false
    private var line: TargetDataLine? = null
    private val bufferSize = 8192  // Increased buffer size
    private val audioBufferQueue = ConcurrentLinkedQueue<ByteArray>()
    private var recordingJob: Job? = null
    private var currentLevel = 0f

    private val audioFormat = AudioFormat(
        44100f,  // Sample rate
        16,      // Sample size in bits
        1,       // Channels (stereo)
        true,    // Signed
        true     // Big endian
    )

    private val audioBuffer = ByteArrayOutputStream()

    override fun startRecording() {
        if (recording) return

        audioBuffer.reset()
        audioBufferQueue.clear()

        try {
            val info = DataLine.Info(TargetDataLine::class.java, audioFormat)
            if (!AudioSystem.isLineSupported(info)) {
                throw LineUnavailableException("Line not supported")
            }

            line = (AudioSystem.getLine(info) as TargetDataLine).apply {
                open(audioFormat)
                start()
            }

            recording = true
            recordingJob = CoroutineScope(Dispatchers.IO).launch {
                val buffer = ByteArray(bufferSize)
                while (recording && isActive) {
                    val count = line?.read(buffer, 0, buffer.size) ?: 0
                    if (count > 0) {
                        val audioData = buffer.copyOfRange(0, count)
                        audioBufferQueue.offer(audioData)
                        audioBuffer.write(audioData, 0, count)
                        currentLevel = calculateRMSLevel(audioData, count)
                    }
                    yield() // Allow other coroutines to execute
                }
            }
        } catch (e: Exception) {
            stopRecording()
            throw e
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
        val audioData = audioBuffer.toByteArray()
        val audioInputStream = AudioInputStream(
            audioData.inputStream(),
            audioFormat,
            audioData.size.toLong() / audioFormat.frameSize
        )

        CoroutineScope(Dispatchers.IO).launch {
            var sourceDataLine: SourceDataLine? = null
            try {
                sourceDataLine = AudioSystem.getSourceDataLine(audioFormat).apply {
                    open(audioFormat, bufferSize)
                    start()
                }

                val playBuffer = ByteArray(bufferSize)
                var bytesRead = 0
                while (bytesRead != -1) {
                    bytesRead = audioInputStream.read(playBuffer, 0, playBuffer.size)
                    if (bytesRead >= 0) {
                        sourceDataLine.write(playBuffer, 0, bytesRead)
                    }
                    yield() // Prevent blocking
                }
            } finally {
                sourceDataLine?.apply {
                    drain()
                    stop()
                    close()
                }
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