package com.musicideas

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.application
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import com.musicideas.ui.navigation.MainViewModel

fun main() = application {
    val appContainer = remember { AppContainer() }
    val mainViewModel = remember { MainViewModel(appContainer) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Music Ideas",
        state = WindowState(width = 1024.dp, height = 768.dp)
    ) {
        MainWindow(mainViewModel)
    }
}