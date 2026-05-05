package com.musicideas.data.audio.recording

import javax.sound.sampled.Mixer

interface AudioRecorder {
    fun startMonitoring()
    fun stopMonitoring()
    fun startRecording()
    fun stopRecording(): ByteArray
    fun getInputLevel(): Float
    val isRecording: Boolean
    fun availableInputDevices(): List<Mixer.Info>
    fun setInputDevice(mixerInfo: Mixer.Info?)
}