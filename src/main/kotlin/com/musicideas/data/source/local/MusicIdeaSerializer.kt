// data/source/local/MusicIdeaSerializer.kt
package com.musicideas.data.source.local

import com.musicideas.domain.model.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
private data class MusicIdeaMetadataDto(
    val genre: Genre,
    val instrument: Instrument,
    val tempo: Int,
    val ideaType: IdeaType,
    val tags: List<String>,
    val createdAt: Long
)

fun serializeMetadata(musicIdea: MusicIdea): String {
    val dto = MusicIdeaMetadataDto(
        genre = musicIdea.metadata.genre,
        instrument = musicIdea.metadata.instrument,
        tempo = musicIdea.metadata.tempo,
        ideaType = musicIdea.metadata.ideaType,
        tags = musicIdea.metadata.tags,
        createdAt = musicIdea.metadata.createdAt
    )
    return Json.encodeToString(MusicIdeaMetadataDto.serializer(), dto)
}

fun deserializeMusicIdea(id: String, audioFile: File, metadataFile: File): MusicIdea {
    val metadata = Json.decodeFromString<MusicIdeaMetadataDto>(metadataFile.readText())
    return MusicIdea(
        id = id,
        audioData = audioFile.readBytes(),
        metadata = MusicIdeaMetadata(
            genre = metadata.genre,
            instrument = metadata.instrument,
            tempo = metadata.tempo,
            ideaType = metadata.ideaType,
            tags = metadata.tags,
            createdAt = metadata.createdAt
        )
    )
}