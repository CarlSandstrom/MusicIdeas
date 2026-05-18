package com.musicideas.data.audio.playback

interface AudioPlayer {
    suspend fun play(audioData: ByteArray, sampleRate: Int)
    fun stop()
    val isPlaying: Boolean
}
