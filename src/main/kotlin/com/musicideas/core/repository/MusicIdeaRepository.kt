package com.musicideas.core.repository

import com.musicideas.core.model.MusicIdea

interface MusicIdeaRepository {
    suspend fun save(musicIdea: MusicIdea): Result<Unit>
    suspend fun getById(id: String): Result<MusicIdea>
    suspend fun getAll(): Result<List<MusicIdea>>
    suspend fun delete(id: String): Result<Unit>
    suspend fun exportToMp3(musicIdea: MusicIdea, outputPath: String): Result<Unit>
}
