package com.musicideas.data.source.local

import com.musicideas.domain.model.MusicIdea
import java.io.File


class MusicIdeaLocalDataSourceImpl(
    private val baseDir: File
) : MusicIdeaLocalDataSource {
    private val audioDir = File(baseDir, "audio")
    private val metadataDir = File(baseDir, "metadata")

    init {
        audioDir.mkdirs()
        metadataDir.mkdirs()
    }

    override suspend fun saveMusicIdea(musicIdea: MusicIdea) {
        val audioFile = File(audioDir, "${musicIdea.id}.raw")
        val audioData = musicIdea.audioDataProvider() // Get audio data only when saving
        audioFile.writeBytes(audioData)
        println("Saved audio file: ${audioFile.absolutePath} with size: ${audioData.size}")

        val metadataFile = File(metadataDir, "${musicIdea.id}.json")
        metadataFile.writeText(serializeMetadata(musicIdea))
    }

    override suspend fun getMusicIdea(id: String): MusicIdea? {
        val metadataFile = File(metadataDir, "$id.json")
        if (!metadataFile.exists()) return null

        return deserializeMusicIdea(id, metadataFile) {
            // Lazy loading function for audio data
            val audioFile = File(audioDir, "$id.raw")
            if (audioFile.exists()) {
                audioFile.readBytes()
            } else {
                ByteArray(0)
            }
        }
    }

    override suspend fun getAllMusicIdeas(): List<MusicIdea> {
        return metadataDir.listFiles()?.mapNotNull { metadataFile ->
            val id = metadataFile.nameWithoutExtension
            deserializeMusicIdea(id, metadataFile) {
                // Lazy loading function for audio data
                val audioFile = File(audioDir, "$id.raw")
                if (audioFile.exists()) {
                    audioFile.readBytes()
                } else {
                    ByteArray(0)
                }
            }
        } ?: emptyList()
    }

    override suspend fun deleteMusicIdea(id: String) {
        TODO("Not yet implemented")
    }
}