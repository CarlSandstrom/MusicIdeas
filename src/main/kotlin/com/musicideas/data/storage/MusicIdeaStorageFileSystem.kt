package com.musicideas.data.storage

import com.musicideas.core.model.MusicIdea
import com.musicideas.data.storage.serialization.deserializeMusicIdea
import com.musicideas.data.storage.serialization.serializeMetadata
import java.io.File


class MusicIdeaStorageFileSystem(
    private val baseDir: File
) : MusicIdeaStorage {
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

        val audioFile = File(audioDir, "$id.raw")
        if (audioFile.exists()) {
            audioFile.delete()
        }
        val metadataFile = File(metadataDir, "$id.json")
        if (metadataFile.exists()) {
            metadataFile.delete()
        }
    }
}