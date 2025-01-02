package com.musicideas.data.repository

import com.musicideas.data.source.local.MusicIdeaLocalDataSource
import com.musicideas.domain.model.MusicIdea
import com.musicideas.domain.repository.MusicIdeaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MusicIdeaRepositoryImpl(
    private val localDataSource: MusicIdeaLocalDataSource
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