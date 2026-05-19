package com.musicideas.core.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val selectedInputDevice: String = "",
    val audioQuality: AudioQuality = AudioQuality.HIGH,
    val saveLocation: String = System.getProperty("user.home") + "/MusicIdeas",
    val darkMode: Boolean = false,
    val defaultGenre: Genre = Genre.ROCK,
    val defaultInstrument: Instrument = Instrument.GUITAR,
    val defaultIdeaType: IdeaType = IdeaType.RIFF
)

@Serializable
enum class AudioQuality {
    LOW, MEDIUM, HIGH
}
