// domain/model/MusicIdea.kt
package com.musicideas.core.model

import java.util.*

data class MusicIdea(
    val id: String,
    val metadata: MusicIdeaMetadata,
    val audioDataProvider: suspend () -> ByteArray
) {
    companion object {
        fun create(
            name: String,
            audioData: ByteArray,
            genre: Genre,
            instrument: Instrument,
            tempo: Int,
            ideaType: IdeaType,
            tags: List<String>,
            rating: Int = 0,
            sampleRate: Int = 44100,
            existingId: String? = null
        ): MusicIdea {
            return MusicIdea(
                id = existingId ?: UUID.randomUUID().toString(),
                metadata = MusicIdeaMetadata(
                    name = name,
                    genre = genre,
                    instrument = instrument,
                    tempo = tempo,
                    ideaType = ideaType,
                    tags = tags,
                    rating = rating,
                    sampleRate = sampleRate,
                    createdAt = System.currentTimeMillis()
                ),
                audioDataProvider = { audioData }
            )
        }
    }
}

data class MusicIdeaMetadata(
    val name: String,
    val genre: Genre,
    val instrument: Instrument,
    val tempo: Int,
    val ideaType: IdeaType,
    val tags: List<String>,
    val rating: Int = 0,
    val sampleRate: Int = 44100,
    val createdAt: Long = System.currentTimeMillis()
)

enum class Genre {
    ROCK, METAL, JAZZ, BLUES, FOLK, CLASSICAL, POP, ELECTRONIC, OTHER, UNKNOWN
}

enum class Instrument {
    GUITAR, BASS, DRUMS, PIANO, SYNTHESIZER, VOCALS, OTHER, UNKNOWN
}

enum class IdeaType {
    RIFF, CHORD_PROGRESSION, SOLO, MELODY, BASSLINE, DRUM_PATTERN, OTHER, UNKNOWN
}