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
        val audioFile = File(audioDir, "${musicIdea.id}.wav")
        audioFile.writeBytes(musicIdea.audioData)

        // For now storing metadata as separate files
        // In real app would use a proper database
        val metadataFile = File(metadataDir, "${musicIdea.id}.json")
        metadataFile.writeText(serializeMetadata(musicIdea))
    }

    override suspend fun getMusicIdea(id: String): MusicIdea? {
        val audioFile = File(audioDir, "$id.wav")
        val metadataFile = File(metadataDir, "$id.json")

        if (!audioFile.exists() || !metadataFile.exists()) return null

        return deserializeMusicIdea(id, audioFile, metadataFile)
    }

    override suspend fun getAllMusicIdeas(): List<MusicIdea> {
        return metadataDir.listFiles()?.mapNotNull { metadataFile ->
            val id = metadataFile.nameWithoutExtension
            val audioFile = File(audioDir, "$id.wav")
            if (audioFile.exists()) {
                deserializeMusicIdea(id, audioFile, metadataFile)
            } else null
        } ?: emptyList()
    }

    override suspend fun deleteMusicIdea(id: String) {
        File(audioDir, "$id.wav").delete()
        File(metadataDir, "$id.json").delete()
    }
}