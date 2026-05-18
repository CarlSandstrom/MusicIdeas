// data/source/local/MusicIdeaSerializer.kt
package com.musicideas.data.storage.serialization

import com.musicideas.core.model.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import java.io.File

@Serializable
private data class MusicIdeaMetadataDto(
    val name: String,
    val genre: Genre,
    val instrument: Instrument,
    val tempo: Int,
    val ideaType: IdeaType,
    val tags: List<String>,
    val rating: Int = 0,
    val sampleRate: Int = 44100,
    val createdAt: Long
)

fun serializeMetadata(musicIdea: MusicIdea): String {
    val dto = MusicIdeaMetadataDto(
        name = musicIdea.metadata.name,
        genre = musicIdea.metadata.genre,
        instrument = musicIdea.metadata.instrument,
        tempo = musicIdea.metadata.tempo,
        ideaType = musicIdea.metadata.ideaType,
        tags = musicIdea.metadata.tags,
        rating = musicIdea.metadata.rating,
        sampleRate = musicIdea.metadata.sampleRate,
        createdAt = musicIdea.metadata.createdAt
    )
    return Json.encodeToString(MusicIdeaMetadataDto.serializer(), dto)
}

fun deserializeMusicIdea(
    id: String,
    metadataFile: File,
    audioDataProvider: suspend () -> ByteArray
): MusicIdea {
    val metadata = try {
        Json.decodeFromString<MusicIdeaMetadataDto>(metadataFile.readText())
    } catch (e: Exception) {
        println("Error deserializing metadata: ${e.message}")
        val jsonObject = Json.parseToJsonElement(metadataFile.readText()).jsonObject
        MusicIdeaMetadataDto(
            name = jsonObject["name"]?.jsonPrimitive?.content ?: "Unknown",
            genre = jsonObject["genre"]?.jsonPrimitive?.content?.let { Genre.valueOf(it) } ?: Genre.UNKNOWN,
            instrument = jsonObject["instrument"]?.jsonPrimitive?.content?.let { Instrument.valueOf(it) } ?: Instrument.UNKNOWN,
            tempo = jsonObject["tempo"]?.jsonPrimitive?.int ?: 0,
            ideaType = jsonObject["ideaType"]?.jsonPrimitive?.content?.let { IdeaType.valueOf(it) } ?: IdeaType.UNKNOWN,
            tags = jsonObject["tags"]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList(),
            rating = jsonObject["rating"]?.jsonPrimitive?.int ?: 0,
            sampleRate = jsonObject["sampleRate"]?.jsonPrimitive?.int ?: 44100,
            createdAt = jsonObject["createdAt"]?.jsonPrimitive?.long ?: 0L
        )
    }

    return MusicIdea(
        id = id,
        metadata = MusicIdeaMetadata(
            name = metadata.name,
            genre = metadata.genre,
            instrument = metadata.instrument,
            tempo = metadata.tempo,
            ideaType = metadata.ideaType,
            tags = metadata.tags,
            rating = metadata.rating,
            sampleRate = metadata.sampleRate,
            createdAt = metadata.createdAt
        ),
        audioDataProvider = audioDataProvider
    )
}