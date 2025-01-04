package com.musicideas.domain.usecase

import com.musicideas.domain.model.*
import com.musicideas.domain.repository.MusicIdeaRepository
import java.util.*

class SaveMusicIdeaUseCase(
    private val repository: MusicIdeaRepository
) {
    suspend operator fun invoke(
        name: String,
        audioData: ByteArray,
        genre: Genre,
        instrument: Instrument,
        tempo: Int,
        ideaType: IdeaType,
        tags: List<String>
    ): Result<Unit> {
        val musicIdea = MusicIdea(
            id = UUID.randomUUID().toString(),
            metadata = MusicIdeaMetadata(
                name = name,
                genre = genre,
                instrument = instrument,
                tempo = tempo,
                ideaType = ideaType,
                tags = tags
            ),
            audioDataProvider = { audioData } // Provide audio data directly when saving
        )
        return repository.save(musicIdea)
    }
}