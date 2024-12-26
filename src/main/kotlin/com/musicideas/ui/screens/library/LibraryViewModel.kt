package com.musicideas.ui.screens.library

import com.musicideas.ui.common.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class LibraryViewModel : ViewModel(){
    private val _state = MutableStateFlow(LibraryScreenState())

}
