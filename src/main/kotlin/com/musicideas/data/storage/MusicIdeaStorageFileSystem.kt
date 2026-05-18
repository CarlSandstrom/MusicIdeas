package com.musicideas.data.storage

import com.musicideas.core.model.MusicIdea
import com.musicideas.data.audio.encoding.Mp3Decoder
import com.musicideas.data.audio.encoding.Mp3Encoder
import com.musicideas.data.storage.serialization.deserializeMusicIdea
import com.musicideas.data.storage.serialization.serializeMetadata
import java.io.File

class MusicIdeaStorageFileSystem(
    private val baseDir: File,
    private val mp3Encoder: Mp3Encoder,
    private val mp3Decoder: Mp3Decoder
) : MusicIdeaStorage {
    private val audioDir = File(baseDir, "audio")
    private val metadataDir = File(baseDir, "metadata")

    init {
        audioDir.mkdirs()
        metadataDir.mkdirs()
    }

    override suspend fun saveMusicIdea(musicIdea: MusicIdea) {
        val mp3Data = mp3Encoder.encodeToBytes(musicIdea.audioDataProvider(), musicIdea.metadata.sampleRate)
        File(audioDir, "${musicIdea.id}.mp3").writeBytes(mp3Data)
        File(metadataDir, "${musicIdea.id}.json").writeText(serializeMetadata(musicIdea))
    }

    private fun loadAudioBytes(id: String): ByteArray {
        val mp3File = File(audioDir, "$id.mp3")
        if (mp3File.exists()) return mp3Decoder.decode(mp3File.readBytes()).pcmBytes
        val rawFile = File(audioDir, "$id.raw")
        return if (rawFile.exists()) rawFile.readBytes() else ByteArray(0)
    }

    override suspend fun getMusicIdea(id: String): MusicIdea? {
        val metadataFile = File(metadataDir, "$id.json")
        if (!metadataFile.exists()) return null
        return deserializeMusicIdea(id, metadataFile) { loadAudioBytes(id) }
    }

    override suspend fun getAllMusicIdeas(): List<MusicIdea> {
        return metadataDir.listFiles()?.mapNotNull { metadataFile ->
            val id = metadataFile.nameWithoutExtension
            deserializeMusicIdea(id, metadataFile) { loadAudioBytes(id) }
        } ?: emptyList()
    }

    override suspend fun deleteMusicIdea(id: String) {
        File(audioDir, "$id.mp3").takeIf { it.exists() }?.delete()
        File(audioDir, "$id.raw").takeIf { it.exists() }?.delete()
        File(metadataDir, "$id.json").takeIf { it.exists() }?.delete()
    }

    override fun getAudioFile(id: String): File? {
        val file = File(audioDir, "$id.mp3")
        return if (file.exists()) file else null
    }
}
