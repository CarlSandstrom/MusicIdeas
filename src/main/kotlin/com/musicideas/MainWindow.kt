package com.musicideas

import androidx.compose.material.MaterialTheme as Material2Theme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import com.musicideas.presentation.navigation.AppNavigation
import com.musicideas.presentation.navigation.MainViewModel
import java.awt.Window

@Composable
fun MainWindow(mainViewModel: MainViewModel, window: Window) {
    val dark = mainViewModel.darkMode
    Material2Theme(colors = if (dark) darkColors() else lightColors()) {
        MaterialTheme(colorScheme = if (dark) darkColorScheme() else lightColorScheme()) {
            AppNavigation(mainViewModel, window)
        }
    }
}
