package com.musicideas.audio.recording

import kotlinx.coroutines.*
import javax.sound.sampled.*
import kotlin.math.sqrt

class JavaSoundRecorder : AudioRecorder {
    private var recording = false
    private var line: TargetDataLine? = null
    private val bufferSize = 4096
    private val buffer = ByteArray(bufferSize)
    private var recordingJob: Job? = null
    private var currentLevel = 0f

    override fun startRecording() {
        if (recording) return

        val format = AudioFormat(44100f, 16, 1, true, true)
        line = AudioSystem.getTargetDataLine(format).apply {
            open(format, bufferSize)
            start()
        }

        recording = true
        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            while (recording) {
                val count = line?.read(buffer, 0, bufferSize) ?: 0
                if (count > 0) {
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