package com.musicideas.presentation.screens.save

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.domain.model.Genre
import com.musicideas.domain.model.IdeaType
import com.musicideas.domain.model.Instrument
import com.musicideas.domain.usecase.SaveMusicIdeaUseCase
import com.musicideas.presentation.common.ViewModel
import kotlinx.coroutines.launch

class SaveViewModel(
    private val audioData: ByteArray,
    private val saveMusicIdeaUseCase: SaveMusicIdeaUseCase,
    private val onSaveComplete: () -> Unit
) : ViewModel() {
    var name by mutableStateOf("")
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
                name = name,
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