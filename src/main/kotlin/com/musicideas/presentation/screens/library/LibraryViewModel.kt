package com.musicideas.presentation.screens.library

import com.musicideas.presentation.common.ViewModel
import com.musicideas.domain.usecase.GetMusicIdeaUseCase
import com.musicideas.domain.model.MusicIdea
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val getMusicIdeaUseCase: GetMusicIdeaUseCase
) : ViewModel() {
    var musicIdeas by mutableStateOf<List<MusicIdea>>(emptyList())
        private set

    init {
        loadMusicIdeas()
    }

    private fun loadMusicIdeas() {
        viewModelScope.launch {
            getMusicIdeaUseCase.getAll().onSuccess { ideas ->
                musicIdeas = ideas
            }
        }
    }
}