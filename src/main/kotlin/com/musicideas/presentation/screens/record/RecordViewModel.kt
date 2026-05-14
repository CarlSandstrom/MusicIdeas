package com.musicideas.presentation.screens.record

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.core.repository.AudioRepository
import com.musicideas.presentation.common.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.sqrt

class RecordViewModel(
    private val audioRepository: AudioRepository
) : ViewModel() {
    var isRecording by mutableStateOf(false)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var currentTimeMs by mutableStateOf(0f)
        private set

    var audioData by mutableStateOf<ByteArray?>(null)
        private set

    var liveBuffer by mutableStateOf(ByteArray(0))
        private set

    var playbackLevel by mutableStateOf(0f)
        private set

    var metronomeEnabled by mutableStateOf(false)
        private set

    var metronomeBpm by mutableStateOf(120)
        private set

    var recordingTempo: Int = 0
        private set

    private var recordingChunksJob: Job? = null
    private var metronomeJob: Job? = null

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        audioRepository.startMonitoring()
    }

    fun clearError() { errorMessage = null }

    fun toggleMetronome() {
        if (metronomeEnabled) {
            stopMetronome()
        } else {
            metronomeEnabled = true
            startMetronomeLoop()
        }
    }

    fun updateMetronomeBpm(bpm: Int) {
        metronomeBpm = bpm.coerceIn(40, 240)
        if (metronomeEnabled) {
            metronomeJob?.cancel()
            startMetronomeLoop()
        }
    }

    fun stopMetronome() {
        metronomeJob?.cancel()
        metronomeJob = null
        metronomeEnabled = false
    }

    private fun startMetronomeLoop() {
        metronomeJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                val clickStart = System.currentTimeMillis()
                playMetronomeClick()
                val elapsed = System.currentTimeMillis() - clickStart
                val remaining = (60_000L / metronomeBpm) - elapsed
                if (remaining > 0) delay(remaining)
            }
        }
    }

    private fun playMetronomeClick() {
        val sampleRate = 44100f
        val durationMs = 30
        val frequency = 880.0
        val numSamples = (sampleRate * durationMs / 1000).toInt()
        val buffer = ByteArray(numSamples * 2)

        for (i in 0 until numSamples) {
            val envelope = when {
                i < numSamples / 8 -> i.toDouble() / (numSamples / 8)
                i > numSamples * 7 / 8 -> (numSamples - i).toDouble() / (numSamples / 8)
                else -> 1.0
            }
            val sample = (sin(2 * PI * frequency * i / sampleRate) * envelope * 28000).toInt().toShort()
            buffer[i * 2] = (sample.toInt() and 0xFF).toByte()
            buffer[i * 2 + 1] = (sample.toInt() shr 8).toByte()
        }

        val format = AudioFormat(sampleRate, 16, 1, true, false)
        val line = AudioSystem.getSourceDataLine(format)
        line.open(format)
        line.start()
        line.write(buffer, 0, buffer.size)
        line.drain()
        line.close()
    }

    fun startRecording() {
        errorMessage = null
        liveBuffer = ByteArray(0)
        recordingTempo = if (metronomeEnabled) metronomeBpm else 0
        viewModelScope.launch {
            audioRepository.startRecording()
                .onSuccess {
                    isRecording = true
                    val accumulator = ByteArrayOutputStream()
                    recordingChunksJob = launch {
                        audioRepository.recordingChunks.collect { chunk ->
                            accumulator.write(chunk)
                            liveBuffer = accumulator.toByteArray()
                        }
                    }
                }
                .onFailure { errorMessage = "Failed to start recording: ${it.message}" }
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            recordingChunksJob?.cancel()
            recordingChunksJob = null
            audioRepository.stopRecording()
                .onSuccess { audio ->
                    audioData = audio
                    liveBuffer = ByteArray(0)
                    isRecording = false
                    audioRepository.startMonitoring()
                }
                .onFailure { errorMessage = "Failed to stop recording: ${it.message}" }
        }
    }

    fun startPlayback() {
        viewModelScope.launch {
            audioData?.let { audio ->
                isPlaying = true
                currentTimeMs = 0f
                val startTime = System.currentTimeMillis()
                val positionJob = launch {
                    while (isPlaying) {
                        currentTimeMs = (System.currentTimeMillis() - startTime).toFloat()
                        playbackLevel = rmsLevelAt(audio, currentTimeMs)
                        delay(50)
                    }
                }
                audioRepository.playAudio(audio)
                positionJob.cancel()
                isPlaying = false
                currentTimeMs = 0f
                playbackLevel = 0f
            }
        }
    }

    fun stopPlayback() {
        viewModelScope.launch {
            audioRepository.stopPlayback()
            isPlaying = false
            currentTimeMs = 0f
        }
    }

    fun getInputLevel(): Float = audioRepository.getInputLevel()

    fun getLevel(): Float = if (isPlaying) playbackLevel else getInputLevel()

    private fun rmsLevelAt(audio: ByteArray, positionMs: Float): Float {
        val byteOffset = (positionMs / 1000f * 44100f * 2).toInt().and(1.inv()).coerceIn(0, audio.size)
        val end = (byteOffset + 4096).coerceAtMost(audio.size).and(1.inv())
        if (end <= byteOffset) return 0f
        var sum = 0.0
        var i = byteOffset
        while (i + 1 < end) {
            val sample = (audio[i + 1].toInt() shl 8) or (audio[i].toInt() and 0xFF)
            sum += sample.toDouble() * sample.toDouble()
            i += 2
        }
        val count = (end - byteOffset) / 2
        return if (count > 0) sqrt(sum / count).toFloat() / 32768f else 0f
    }

    override fun onCleared() {
        metronomeJob?.cancel()
        audioRepository.stopMonitoring()
        super.onCleared()
    }
}
