package com.musicideas.presentation.screens.library

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.domain.model.Genre
import com.musicideas.domain.model.IdeaType
import com.musicideas.domain.model.Instrument
import com.musicideas.domain.model.MusicIdea
import com.musicideas.domain.usecase.GetMusicIdeaUseCase
import com.musicideas.domain.usecase.PlaybackMusicUseCase
import com.musicideas.presentation.common.ViewModel
import kotlinx.coroutines.launch

data class FilterState(
    val selectedGenre: Genre? = null,
    val selectedInstrument: Instrument? = null,
    val selectedIdeaType: IdeaType? = null,
    val timeRange: ClosedRange<Long> = (System.currentTimeMillis() - 365L * 24 * 60 * 60 * 1000)..System.currentTimeMillis(),
    val tagSearchQuery: String = "",
    val selectedTags: Set<String> = emptySet()
)

class LibraryViewModel(
    private val getMusicIdeaUseCase: GetMusicIdeaUseCase,
    private val playbackMusicUseCase: PlaybackMusicUseCase // Add this dependency
) : ViewModel() {

    var showDeleteConfirmation by mutableStateOf(false)
        private set
    var musicIdeaToDelete by mutableStateOf<String?>(null)
        private set

    private var _musicIdeas by mutableStateOf<List<MusicIdea>>(emptyList())
    private var _filterState by mutableStateOf(FilterState())

    val musicIdeas: List<MusicIdea> get() = _musicIdeas
    val filterState: FilterState get() = _filterState

    val availableTags by derivedStateOf {
        _musicIdeas.flatMap { it.metadata.tags }.distinct().sorted()
    }

    val filteredMusicIdeas by derivedStateOf {
        var filtered = _musicIdeas

        // Filter by genre
        _filterState.selectedGenre?.let { genre ->
            filtered = filtered.filter { it.metadata.genre == genre }
        }

        // Filter by instrument
        _filterState.selectedInstrument?.let { instrument ->
            filtered = filtered.filter { it.metadata.instrument == instrument }
        }

        // Filter by idea type
        _filterState.selectedIdeaType?.let { ideaType ->
            filtered = filtered.filter { it.metadata.ideaType == ideaType }
        }

        // Filter by time range
        filtered = filtered.filter {
            it.metadata.createdAt in _filterState.timeRange.start.._filterState.timeRange.endInclusive
        }

        // Filter by tags (from both search and checkboxes)
        val searchTags = _filterState.tagSearchQuery.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .toSet()

        val allSelectedTags = searchTags + _filterState.selectedTags

        if (allSelectedTags.isNotEmpty()) {
            filtered = filtered.filter { musicIdea ->
                musicIdea.metadata.tags.any { tag ->
                    allSelectedTags.any { selectedTag ->
                        tag.contains(selectedTag, ignoreCase = true)
                    }
                }
            }
        }

        filtered
    }

    init {
        loadMusicIdeas()
    }

    private fun loadMusicIdeas() {
        viewModelScope.launch {
            getMusicIdeaUseCase.getAll().onSuccess { ideas ->
                _musicIdeas = ideas
            }
        }
    }

    fun updateFilter(update: FilterState.() -> FilterState) {
        _filterState = _filterState.update()
    }

    private var _selectedMusicIdeaId by mutableStateOf<String?>(null)
    val selectedMusicIdeaId: String? get() = _selectedMusicIdeaId

    private var _playingMusicIdeaId by mutableStateOf<String?>(null)
    val playingMusicIdeaId: String? get() = _playingMusicIdeaId

    fun selectMusicIdea(id: String) {
        _selectedMusicIdeaId = if (_selectedMusicIdeaId == id) null else id
    }

    fun playMusicIdea(id: String) {
        viewModelScope.launch {
            _playingMusicIdeaId = id
            val musicIdea = musicIdeas.find { it.id == id }
            if (musicIdea != null) {
                try {
                    val audioData = musicIdea.audioDataProvider() // Load audio data only when needed
                    if (audioData.isEmpty()) {
                        println("Audio data is empty for music idea: $id")
                        return@launch
                    }
                    println("Playing music idea: $id with audio size: ${audioData.size}")
                    playbackMusicUseCase.startPlayback(audioData)
                        .onSuccess {
                            println("Successfully started playback")
                        }
                        .onFailure { error ->
                            println("Failed to start playback: ${error.message}")
                            _playingMusicIdeaId = null
                        }
                } catch (e: Exception) {
                    println("Error loading audio data: ${e.message}")
                    _playingMusicIdeaId = null
                }
            } else {
                println("Music idea not found: $id")
            }
        }
    }

    fun stopPlayback() {
        viewModelScope.launch {
            playbackMusicUseCase.stopPlayback()
            _playingMusicIdeaId = null
        }
    }

    fun shareMusicIdea(id: String) {
        // Implement sharing functionality
        // This could open a dialog or handle the sharing process
    }

    fun promptDeleteMusicIdea(id: String) {
        musicIdeaToDelete = id
        showDeleteConfirmation = true
    }

    fun confirmDelete() {
        musicIdeaToDelete?.let { id ->
            viewModelScope.launch {
                getMusicIdeaUseCase.delete(id).onSuccess {
                    loadMusicIdeas()
                }
            }
        }
        showDeleteConfirmation = false
        musicIdeaToDelete = null
    }

    fun dismissDeleteDialog() {
        showDeleteConfirmation = false
        musicIdeaToDelete = null
    }

}