package com.musicideas.data.audio.config

import javax.sound.sampled.AudioFormat

object AudioFormatConfig {
    val format = AudioFormat(
        44100f,  // Sample rate
        16,      // Sample size in bits
        1,       // Channels (mono)
        true,    // Signed
        false    // Little endian (required for Windows audio drivers)
    )
}