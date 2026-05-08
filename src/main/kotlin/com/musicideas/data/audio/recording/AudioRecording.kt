package com.musicideas.data.audio.recording

import kotlinx.coroutines.flow.Flow
import javax.sound.sampled.Mixer

interface AudioRecorder {
    fun startMonitoring()
    fun stopMonitoring()
    fun startRecording()
    fun stopRecording(): ByteArray
    fun getInputLevel(): Float
    val isRecording: Boolean
    val recordingChunks: Flow<ByteArray>
    fun availableInputDevices(): List<Mixer.Info>
    fun setInputDevice(mixerInfo: Mixer.Info?)
}