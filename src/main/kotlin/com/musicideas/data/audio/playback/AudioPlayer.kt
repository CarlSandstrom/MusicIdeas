package com.musicideas.data.audio.playback

interface AudioPlayer {
    suspend fun play(audioData: ByteArray)
    fun stop()
    val isPlaying: Boolean
}
