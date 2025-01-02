package com.musicideas.presentation.screens.save

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import com.musicideas.domain.usecase.SaveMusicIdeaUseCase
import com.musicideas.domain.model.*
import com.musicideas.presentation.common.ViewModel

class SaveViewModel(
    private val audioData: ByteArray,
    private val saveMusicIdeaUseCase: SaveMusicIdeaUseCase,
    private val onSaveComplete: () -> Unit
) : ViewModel() {
    var genre by mutableStateOf(Genre.ROCK)
    var instrument by mutableStateOf(Instrument.GUITAR)
    var tempo by mutableStateOf(120)
    var ideaType by mutableStateOf(IdeaType.RIFF)
    var customTags by mutableStateOf("")
    var tempoString by mutableStateOf("120")

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
                audioData = audioData,
                genre = genre,
                instrument = instrument,
                tempo = tempo,
                ideaType = ideaType,
                tags = customTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
            ).onSuccess {
                onSaveComplete()
            }
        }
    }
}