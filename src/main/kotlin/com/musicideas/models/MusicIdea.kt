package com.musicideas.models

data class MusicIdea(
    val audioData: ByteArray,
    val genre: Genre,
    val instrument: Instrument,
    val tempo: Int,
    val ideaType: IdeaType,
    val customTags: List<String>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as MusicIdea
        return audioData.contentEquals(other.audioData) &&
                genre == other.genre &&
                instrument == other.instrument &&
                tempo == other.tempo &&
                ideaType == other.ideaType &&
                customTags == other.customTags
    }

    override fun hashCode(): Int {
        var result = audioData.contentHashCode()
        result = 31 * result + genre.hashCode()
        result = 31 * result + instrument.hashCode()
        result = 31 * result + tempo
        result = 31 * result + ideaType.hashCode()
        result = 31 * result + customTags.hashCode()
        return result
    }
}

enum class Genre {
    ROCK, METAL, JAZZ, BLUES, FOLK, CLASSICAL, POP, ELECTRONIC, OTHER
}

enum class Instrument {
    GUITAR, BASS, DRUMS, PIANO, SYNTHESIZER, VOCALS, OTHER
}

enum class IdeaType {
    RIFF, CHORD_PROGRESSION, SOLO, MELODY, BASSLINE, DRUM_PATTERN, OTHER
}
