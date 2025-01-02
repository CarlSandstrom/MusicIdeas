package com.musicideas.domain.repository

import com.musicideas.domain.model.MusicIdea

interface MusicIdeaRepository {
    suspend fun save(musicIdea: MusicIdea): Result<Unit>
    suspend fun getById(id: String): Result<MusicIdea>
    suspend fun getAll(): Result<List<MusicIdea>>
    suspend fun delete(id: String): Result<Unit>
}
