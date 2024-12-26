package com.musicideas


import androidx.compose.runtime.*
import androidx.compose.material3.MaterialTheme
import com.musicideas.ui.navigation.AppNavigation
import com.musicideas.ui.navigation.MainViewModel

@Composable
fun MainWindow(mainViewModel: MainViewModel) {
    MaterialTheme {
        AppNavigation(mainViewModel)
    }
}
