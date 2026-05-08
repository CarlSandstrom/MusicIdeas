package com.musicideas.data.audio.recording

import com.musicideas.data.audio.config.AudioFormatConfig
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.ByteArrayOutputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.LineUnavailableException
import javax.sound.sampled.Mixer
import javax.sound.sampled.TargetDataLine
import kotlin.math.sqrt

open class JavaSoundRecorder : AudioRecorder {
    private var _isRecording = false
    override val isRecording: Boolean get() = _isRecording

    private val _recordingChunks = MutableSharedFlow<ByteArray>(extraBufferCapacity = 128)
    override val recordingChunks: Flow<ByteArray> = _recordingChunks.asSharedFlow()

    private var selectedMixerInfo: Mixer.Info? = null
    private var line: TargetDataLine? = null
    private val bufferSize = 8192
    private var recordingJob: Job? = null
    private var monitoringJob: Job? = null
    private var isMonitoring = false
    private var currentLevel = 0f
    private val audioBuffer = ByteArrayOutputStream()

    override fun availableInputDevices(): List<Mixer.Info> {
        val lineInfo = DataLine.Info(TargetDataLine::class.java, AudioFormatConfig.format)
        return AudioSystem.getMixerInfo().filter { mixerInfo ->
            try {
                val mixer = AudioSystem.getMixer(mixerInfo)
                mixer.isLineSupported(lineInfo)
            } catch (_: Exception) {
                false
            }
        }
    }

    override fun setInputDevice(mixerInfo: Mixer.Info?) {
        selectedMixerInfo = mixerInfo
        if (isMonitoring) {
            stopMonitoring()
            startMonitoring()
        }
    }

    private fun openLine() {
        val info = DataLine.Info(TargetDataLine::class.java, AudioFormatConfig.format)
        line = (selectedMixerInfo
            ?.let { AudioSystem.getMixer(it).getLine(info) as TargetDataLine }
            ?: run {
                if (!AudioSystem.isLineSupported(info)) throw LineUnavailableException("Line not supported")
                AudioSystem.getLine(info) as TargetDataLine
            }).apply {
            open(AudioFormatConfig.format)
            start()
        }
    }

    override fun startMonitoring() {
        if (isMonitoring || isRecording) return
        try {
            openLine()
            isMonitoring = true
            monitoringJob = CoroutineScope(Dispatchers.IO).launch {
                val buffer = ByteArray(bufferSize)
                while (isMonitoring && isActive) {
                    val count = line?.read(buffer, 0, buffer.size) ?: 0
                    if (count > 0) currentLevel = calculateRMSLevel(buffer, count)
                    yield()
                }
            }
        } catch (e: Exception) {
            println("Error starting monitoring: ${e.message}")
        }
    }

    override fun stopMonitoring() {
        isMonitoring = false
        monitoringJob?.cancel()
        monitoringJob = null
        line?.apply { stop(); close() }
        line = null
        currentLevel = 0f
    }

    override fun startRecording() {
        if (isRecording) return
        stopMonitoring()
        audioBuffer.reset()
        try {
            println("Recording with device: ${selectedMixerInfo?.name ?: "system default"}")
            openLine()
            _isRecording = true
            recordingJob = CoroutineScope(Dispatchers.IO).launch {
                val buffer = ByteArray(bufferSize)
                while (isRecording && isActive) {
                    val count = line?.read(buffer, 0, buffer.size) ?: 0
                    if (count > 0) {
                        audioBuffer.write(buffer, 0, count)
                        currentLevel = calculateRMSLevel(buffer, count)
                        _recordingChunks.tryEmit(buffer.copyOf(count))
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
        line?.apply { stop(); close() }
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
