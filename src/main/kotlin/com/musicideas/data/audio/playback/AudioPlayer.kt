package com.musicideas.data.audio.playback

interface AudioPlayer {
    fun play(audioData: ByteArray)
    fun stop()
    val isPlaying: Boolean
}
