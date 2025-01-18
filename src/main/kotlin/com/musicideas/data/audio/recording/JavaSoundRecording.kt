package com.musicideas.data.audio.recording

import com.musicideas.data.audio.config.AudioFormatConfig
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.TargetDataLine
import kotlin.math.sqrt

open class JavaSoundRecorder : AudioRecorder {
    private var _isRecording = false
    override val isRecording: Boolean get() = _isRecording

    private var line: TargetDataLine? = null
    private val bufferSize = 8192
    private var recordingJob: Job? = null
    private var currentLevel = 0f
    private val audioBuffer = ByteArrayOutputStream()

    override fun startRecording() {
        if (isRecording) return

        audioBuffer.reset()
        try {
            val info = DataLine.Info(TargetDataLine::class.java, AudioFormatConfig.format)
            if (!AudioSystem.isLineSupported(info)) {
                throw LineUnavailableException("Line not supported")
            }

            line = (AudioSystem.getLine(info) as TargetDataLine).apply {
                open(AudioFormatConfig.format)
                start()
            }

            _isRecording = true
            recordingJob = CoroutineScope(Dispatchers.IO).launch {
                val buffer = ByteArray(bufferSize)
                while (isRecording && isActive) {
                    val count = line?.read(buffer, 0, buffer.size) ?: 0
                    if (count > 0) {
                        audioBuffer.write(buffer, 0, count)
                        currentLevel = calculateRMSLevel(buffer, count)
                    }
                    yield()
                }
            }
        } catch (e: Exception) {
            println("Error starting recording: ${e.message}")
            stopRecording()
            throw e
        }
    }

    override fun stopRecording(): ByteArray {
        _isRecording = false
        recordingJob?.cancel()
        line?.apply {
            stop()
            close()
        }
        line = null
        return audioBuffer.toByteArray()
    }

    override fun getInputLevel(): Float = currentLevel

    private fun calculateRMSLevel(buffer: ByteArray, count: Int): Float {
        var sum = 0.0
        for (i in 0 until count step 2) {
            val sample = (buffer[i + 1].toInt() shl 8) or (buffer[i].toInt() and 0xFF)
            sum += (sample * sample).toDouble()
        }
        return sqrt(sum / (count / 2)).toFloat() / 32768f
    }
}