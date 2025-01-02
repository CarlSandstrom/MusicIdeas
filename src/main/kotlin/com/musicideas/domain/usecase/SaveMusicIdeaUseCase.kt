package com.musicideas.domain.usecase

import com.musicideas.domain.model.*
import com.musicideas.domain.repository.MusicIdeaRepository
import java.util.UUID

class SaveMusicIdeaUseCase(
    private val repository: MusicIdeaRepository
) {
    suspend operator fun invoke(
        audioData: ByteArray,
        genre: Genre,
        instrument: Instrument,
        tempo: Int,
        ideaType: IdeaType,
        tags: List<String>
    ): Result<Unit> {
        val musicIdea = MusicIdea(
            id = UUID.randomUUID().toString(),
            audioData = audioData,
            metadata = MusicIdeaMetadata(
                genre = genre,
                instrument = instrument,
                tempo = tempo,
                ideaType = ideaType,
                tags = tags
            )
        )
        return repository.save(musicIdea)
    }
}