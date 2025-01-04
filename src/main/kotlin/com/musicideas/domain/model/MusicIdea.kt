// domain/model/MusicIdea.kt
package com.musicideas.domain.model

data class MusicIdea(
    val id: String, // Added id for uniqueness
    val metadata: MusicIdeaMetadata,
    val audioDataProvider : suspend () -> ByteArray // Added audioDataProvider for lazy loading
)

data class MusicIdeaMetadata(
    val genre: Genre,
    val instrument: Instrument,
    val tempo: Int,
    val ideaType: IdeaType,
    val tags: List<String>,
    val createdAt: Long = System.currentTimeMillis()
)

enum class Genre {
    ROCK, METAL, JAZZ, BLUES, FOLK, CLASSICAL, POP, ELECTRONIC, OTHER
}

enum class Instrument {
    GUITAR, BASS, DRUMS, PIANO, SYNTHESIZER, VOCALS, OTHER
}

enum class IdeaType {
    RIFF, CHORD_PROGRESSION, SOLO, MELODY, BASSLINE, DRUM_PATTERN, OTHER
}