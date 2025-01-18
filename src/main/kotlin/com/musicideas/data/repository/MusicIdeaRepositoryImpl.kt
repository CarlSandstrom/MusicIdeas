package com.musicideas.data.repository

import com.musicideas.data.storage.MusicIdeaStorage
import com.musicideas.core.model.MusicIdea
import com.musicideas.core.repository.MusicIdeaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicIdeaRepositoryImpl(
    private val localDataSource: MusicIdeaStorage
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
}