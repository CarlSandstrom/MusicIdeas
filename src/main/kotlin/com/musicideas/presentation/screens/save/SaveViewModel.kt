package com.musicideas.presentation.screens.save

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.core.model.Genre
import com.musicideas.core.model.IdeaType
import com.musicideas.core.model.Instrument
import com.musicideas.core.model.MusicIdea
import com.musicideas.core.repository.MusicIdeaRepository
import com.musicideas.presentation.common.ViewModel
import kotlinx.coroutines.launch
import java.util.*

class SaveViewModel(
    private val audioData: ByteArray,
    private val repository: MusicIdeaRepository,
    private val onSaveComplete: () -> Unit,
    existingMusicIdea: MusicIdea? = null,
    initialTempo: Int = 120,
    initialName: String = "",
    initialSampleRate: Int = 44100,
    defaultGenre: Genre = Genre.ROCK,
    defaultInstrument: Instrument = Instrument.GUITAR
) : ViewModel() {
    var id by mutableStateOf(existingMusicIdea?.id ?: UUID.randomUUID().toString())
    var name by mutableStateOf(existingMusicIdea?.metadata?.name ?: initialName)
    var genre by mutableStateOf(existingMusicIdea?.metadata?.genre ?: defaultGenre)
    var instrument by mutableStateOf(existingMusicIdea?.metadata?.instrument ?: defaultInstrument)
    private val defaultTempo = existingMusicIdea?.metadata?.tempo ?: initialTempo
    var tempo by mutableStateOf(defaultTempo)
    var ideaType by mutableStateOf(existingMusicIdea?.metadata?.ideaType ?: IdeaType.RIFF)
    var customTags by mutableStateOf(existingMusicIdea?.metadata?.tags?.joinToString(",") ?: "")
    var tempoString by mutableStateOf(defaultTempo.toString())
    var rating by mutableStateOf(existingMusicIdea?.metadata?.rating ?: 0)

    private val sampleRate = existingMusicIdea?.metadata?.sampleRate ?: initialSampleRate
    val isEditing = existingMusicIdea != null

    var saveError by mutableStateOf<String?>(null)
        private set

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
            val musicIdea = MusicIdea.create(
                name = name,
                audioData = audioData,
                genre = genre,
                instrument = instrument,
                tempo = tempo,
                ideaType = ideaType,
                tags = customTags.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                rating = rating,
                sampleRate = sampleRate,
                existingId = id
            )
            repository.save(musicIdea)
                .onSuccess { onSaveComplete() }
                .onFailure { saveError = it.message ?: "Save failed" }
        }
    }
}