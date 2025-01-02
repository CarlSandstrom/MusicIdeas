package com.musicideas.presentation.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

open class ViewModel {
    protected val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    open fun onCleared() {
        viewModelScope.cancel()
    }
}