package com.musicideas.presentation.screens.save

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.domain.model.Genre
import com.musicideas.domain.model.IdeaType
import com.musicideas.domain.model.Instrument
import com.musicideas.domain.model.MusicIdea
import com.musicideas.domain.usecase.SaveMusicIdeaUseCase
import com.musicideas.presentation.common.ViewModel
import kotlinx.coroutines.launch
import java.util.*

class SaveViewModel(
    private val audioData: ByteArray,
    private val saveMusicIdeaUseCase: SaveMusicIdeaUseCase,
    private val onSaveComplete: () -> Unit,
    existingMusicIdea: MusicIdea? = null
) : ViewModel() {
    var id by mutableStateOf(existingMusicIdea?.id ?: UUID.randomUUID().toString())
    var name by mutableStateOf(existingMusicIdea?.metadata?.name ?: "")
    var genre by mutableStateOf(existingMusicIdea?.metadata?.genre ?: Genre.ROCK)
    var instrument by mutableStateOf(existingMusicIdea?.metadata?.instrument ?: Instrument.GUITAR)
    var tempo by mutableStateOf(existingMusicIdea?.metadata?.tempo ?: 120)
    var ideaType by mutableStateOf(existingMusicIdea?.metadata?.ideaType ?: IdeaType.RIFF)
    var customTags by mutableStateOf(existingMusicIdea?.metadata?.tags?.joinToString(",") ?: "")
    var tempoString by mutableStateOf((existingMusicIdea?.metadata?.tempo ?: 120).toString())

    val isEditing = existingMusicIdea != null

    fun updateTempo(newTempoStr: String) {
        tempoString = newTempoStr
        newTempoStr.toIntOrNull()?.let { newTempo ->
            if (newTempo in 1..300) {
                tempo = newTempo
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            saveMusicIdeaUseCase(
                name = name,
                audioData = audioData,
                genre = genre,
                instrument = instrument,
                tempo = tempo,
                ideaType = ideaType,
                tags = customTags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                existingId = id
            ).onSuccess {
                onSaveComplete()
            }
        }
    }
}