package com.musicideas.presentation.screens.cloudstorage

import com.musicideas.presentation.common.ViewModel
import com.musicideas.domain.model.MusicIdea
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

class CloudStorageViewModel : ViewModel() {
    var musicIdeas by mutableStateOf<List<MusicIdea>>(emptyList())
        private set
    var isUploading by mutableStateOf(false)
        private set

    fun uploadMusicIdea(musicIdea: MusicIdea) {
        viewModelScope.launch {
            isUploading = true
            // Cloud storage implementation here
            isUploading = false
        }
    }
}
