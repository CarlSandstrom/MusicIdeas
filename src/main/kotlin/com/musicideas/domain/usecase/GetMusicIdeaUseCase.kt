package com.musicideas.domain.usecase

import com.musicideas.domain.model.MusicIdea
import com.musicideas.domain.repository.MusicIdeaRepository

class GetMusicIdeaUseCase(
    private val repository: MusicIdeaRepository
) {
    suspend fun getById(id: String): Result<MusicIdea> =
        repository.getById(id)

    suspend fun getAll(): Result<List<MusicIdea>> =
        repository.getAll()

    suspend fun delete(id: String): Result<Unit> =
        repository.delete(id)
}