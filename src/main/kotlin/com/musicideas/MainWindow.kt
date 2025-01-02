package com.musicideas


import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.musicideas.presentation.navigation.AppNavigation
import com.musicideas.presentation.navigation.MainViewModel

@Composable
fun MainWindow(mainViewModel: MainViewModel) {
    MaterialTheme {
        AppNavigation(mainViewModel)
    }
}
