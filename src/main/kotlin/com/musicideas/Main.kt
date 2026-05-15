// Main.kt
package com.musicideas

import androidx.compose.ui.window.application
import androidx.compose.ui.window.Window
import org.bytedeco.ffmpeg.global.avutil
import org.koin.core.context.startKoin
import com.musicideas.di.appModule
import com.musicideas.presentation.navigation.MainViewModel
import org.koin.compose.getKoin

fun main() = application {
    avutil.av_log_set_level(avutil.AV_LOG_ERROR)

    startKoin {
        modules(appModule)
    }

    val mainViewModel = getKoin().get<MainViewModel>()

    Window(
        onCloseRequest = ::exitApplication,
        title = "Music Ideas"
    ) {
        MainWindow(mainViewModel)
    }
}