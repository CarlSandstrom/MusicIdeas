package com.musicideas.ui.screens.save

import androidx.compose.runtime.getValue
import com.musicideas.models.MusicIdea
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.models.Genre
import com.musicideas.models.IdeaType
import com.musicideas.models.Instrument

class SaveViewModel(
    private val audioData: ByteArray,
    private val onSaveComplete: () -> Unit
) {
    var genre by mutableStateOf(Genre.ROCK)
    var instrument by mutableStateOf(Instrument.GUITAR)
    var tempo by mutableStateOf(120) // Default to 120 BPM
    var ideaType by mutableStateOf(IdeaType.RIFF)
    var customTags by mutableStateOf("")
    var tempoString by mutableStateOf("120") // For handling text input

    fun updateTempo(newTempoStr: String) {
        tempoString = newTempoStr
        newTempoStr.toIntOrNull()?.let { newTempo ->
            if (newTempo in 1..300) { // Reasonable BPM range
                tempo = newTempo
            }
        }
    }

    fun save() {
        val musicIdea = MusicIdea(
            audioData = audioData,
            genre = genre,
            instrument = instrument,
            tempo = tempo,
            ideaType = ideaType,
            customTags = customTags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        )
        // TODO: Save to repository
        print("Save")
        onSaveComplete()
    }
}