package com.musicideas.ui.screens.record

import com.musicideas.ui.common.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecordViewModel : ViewModel() {
    private val _state = MutableStateFlow(RecordScreenState())
    val state = _state.asStateFlow()

    // Add your screen-specific logic here
}