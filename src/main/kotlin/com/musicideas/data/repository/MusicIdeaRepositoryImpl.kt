package com.musicideas.data.repository

import com.musicideas.core.model.MusicIdea
import com.musicideas.core.repository.MusicIdeaRepository
import com.musicideas.data.audio.encoding.Mp3Encoder
import com.musicideas.data.storage.MusicIdeaStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MusicIdeaRepositoryImpl(
    private val localDataSource: MusicIdeaStorage,
    private val mp3Encoder: Mp3Encoder
) : MusicIdeaRepository {
    override suspend fun save(musicIdea: MusicIdea): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            localDataSource.saveMusicIdea(musicIdea)
        }
    }

    override suspend fun getById(id: String): Result<MusicIdea> = withContext(Dispatchers.IO) {
        runCatching {
            localDataSource.getMusicIdea(id) ?: throw IllegalStateException("Music idea not found")
        }
    }

    override suspend fun getAll(): Result<List<MusicIdea>> = withContext(Dispatchers.IO) {
        runCatching {
            localDataSource.getAllMusicIdeas()
        }
    }

    override suspend fun delete(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            localDataSource.deleteMusicIdea(id)
        }
    }

    override suspend fun exportToMp3(musicIdea: MusicIdea, outputPath: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            mp3Encoder.encode(musicIdea, outputPath)
        }
    }

    override fun getAudioFile(id: String): File? = localDataSource.getAudioFile(id)
}