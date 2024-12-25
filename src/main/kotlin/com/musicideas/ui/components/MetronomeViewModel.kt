package com.musicideas.ui.components

import com.musicideas.audio.AudioEngine
import kotlinx.coroutines.*

class MetronomeViewModel(private val audioEngine: AudioEngine) {
    private var bpm = 120
    private var isRunning = false
    private var tickJob: Job? = null

    fun setBPM(value: Int) {
        bpm = value
        if (isRunning) {
            restart()
        }
    }

    fun start() {
        if (!isRunning) {
            isRunning = true
            tickJob = CoroutineScope(Dispatchers.Default).launch {
                while (isRunning) {
                    audioEngine.playSound(440f, 50) // A4 note, 50ms duration
                    delay((60_000 / bpm).toLong())
                }
            }
        }
    }

    fun stop() {
        isRunning = false
        tickJob?.cancel()
        tickJob = null
    }

    private fun restart() {
        stop()
        start()
    }
}