package com.musicideas.presentation.screens.library

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.musicideas.core.model.Genre
import com.musicideas.core.model.IdeaType
import com.musicideas.core.model.Instrument
import com.musicideas.core.model.MusicIdea
import com.musicideas.core.repository.AudioRepository
import com.musicideas.core.repository.ExportDialog
import com.musicideas.core.repository.MusicIdeaRepository
import com.musicideas.presentation.common.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

data class AudioImport(val name: String, val audioData: ByteArray, val sampleRate: Int)

data class FilterState(
    val selectedGenre: Genre? = null,
    val selectedInstrument: Instrument? = null,
    val selectedIdeaType: IdeaType? = null,
    var timeRange: ClosedRange<Long> = 0L..System.currentTimeMillis(),
    var fullTimeRange: ClosedRange<Long> = 0L..System.currentTimeMillis(),
    val tagSearchQuery: String = "",
    val selectedTags: Set<String> = emptySet(),
    val minRating: Int = 0,
    val titleQuery: String = ""
)

class LibraryViewModel(
    private val musicIdeaRepository: MusicIdeaRepository,
    private val audioRepository: AudioRepository,
    private val exportDialog: ExportDialog
) : ViewModel() {

    var showDeleteConfirmation by mutableStateOf(false)
        private set
    private var musicIdeaToDelete by mutableStateOf<String?>(null)

    private var _musicIdeas by mutableStateOf<List<MusicIdea>>(emptyList())
    private var _filterState by mutableStateOf(FilterState())

    private val musicIdeas: List<MusicIdea> get() = _musicIdeas
    val filterState: FilterState get() = _filterState

    var selectedMusicIdeaId by mutableStateOf<String?>(null)
        private set

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

        // Filter by minimum rating
        if (_filterState.minRating > 0) {
            filtered = filtered.filter { it.metadata.rating >= _filterState.minRating }
        }

        // Filter by title
        if (_filterState.titleQuery.isNotEmpty()) {
            filtered = filtered.filter {
                it.metadata.name.contains(_filterState.titleQuery, ignoreCase = true)
            }
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

    fun reload() = loadMusicIdeas()

    private fun loadMusicIdeas() {
        viewModelScope.launch {
            musicIdeaRepository.getAll().onSuccess { ideas ->
                _musicIdeas = ideas

                // Update the time range based on actual recordings
                if (ideas.isNotEmpty()) {
                    val oldestRecording = ideas.minOf { it.metadata.createdAt }
                    val newestRecording = ideas.maxOf { it.metadata.createdAt }
                    _filterState = _filterState.copy(
                        timeRange = oldestRecording..newestRecording,
                        fullTimeRange = oldestRecording..newestRecording
                    )
                }
            }
        }
    }

    fun exportMusicIdea(id: String) {
        viewModelScope.launch {
            val path = exportDialog.show() ?: return@launch
            val idea = musicIdeas.find { it.id == id } ?: return@launch
            musicIdeaRepository.exportToMp3(idea, path)
        }
    }

    fun updateFilter(update: FilterState.() -> FilterState) {
        _filterState = _filterState.update()
    }

    private var _selectedMusicIdeaId by mutableStateOf<String?>(null)

    fun selectMusicIdea(id: String) {
        _selectedMusicIdeaId = if (_selectedMusicIdeaId == id) null else id
    }

    private var _playingMusicIdeaId by mutableStateOf<String?>(null)
    val playingMusicIdeaId: String? get() = _playingMusicIdeaId

    fun playMusicIdea(id: String) {
        viewModelScope.launch {
            if (_playingMusicIdeaId == id) { stopPlayback(); return@launch }
            if (_playingMusicIdeaId != null) stopPlayback()

            val musicIdea = musicIdeas.find { it.id == id } ?: return@launch
            try {
                val audioData = musicIdea.audioDataProvider()
                if (audioData.isEmpty()) return@launch
                _playingMusicIdeaId = id
                audioRepository.playAudio(audioData, musicIdea.metadata.sampleRate)
                    .onFailure { _playingMusicIdeaId = null }
            } catch (e: Exception) {
                _playingMusicIdeaId = null
            }
        }
    }

    fun stopPlayback() {
        viewModelScope.launch {
            audioRepository.stopPlayback()
            _playingMusicIdeaId = null
        }
    }

    fun getAudioFile(id: String): File? = musicIdeaRepository.getAudioFile(id)

    fun promptDeleteMusicIdea(id: String) {
        musicIdeaToDelete = id
        showDeleteConfirmation = true
    }

    fun confirmDelete() {
        musicIdeaToDelete?.let { id ->
            viewModelScope.launch {
                musicIdeaRepository.delete(id).onSuccess {
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

    fun importFiles(entries: List<AudioImport>) {
        viewModelScope.launch(Dispatchers.IO) {
            entries.forEach { (name, audioData, sampleRate) ->
                val idea = MusicIdea.create(
                    name = name,
                    audioData = audioData,
                    genre = Genre.UNKNOWN,
                    instrument = Instrument.UNKNOWN,
                    tempo = 0,
                    ideaType = IdeaType.UNKNOWN,
                    tags = emptyList(),
                    sampleRate = sampleRate,
                    durationMs = audioData.size.toLong() * 1000L / 2 / sampleRate
                )
                musicIdeaRepository.save(idea)
            }
            loadMusicIdeas()
        }
    }

    fun editMusicIdea(musicIdea: MusicIdea, onEdit: (MusicIdea) -> Unit) {
        viewModelScope.launch {
            // Load the audio data before editing
            val audioData = musicIdea.audioDataProvider()
            // Create a new MusicIdea instance with the loaded audio data
            val ideaWithLoadedAudio = MusicIdea(
                id = musicIdea.id,
                metadata = musicIdea.metadata,
                audioDataProvider = { audioData }
            )
            onEdit(ideaWithLoadedAudio)
        }
    }
}