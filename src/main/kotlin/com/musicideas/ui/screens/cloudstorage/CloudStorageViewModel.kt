package com.musicideas.ui.screens.cloudstorage

import com.musicideas.ui.common.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class CloudStorageViewModel : ViewModel(){
    private val _state = MutableStateFlow(CloudStorageScreenState())

}
