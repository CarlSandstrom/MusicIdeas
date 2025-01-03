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
    private val getMusicIdeaUseCase: GetMusicIdeaUseCase
) : ViewModel() {
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
}