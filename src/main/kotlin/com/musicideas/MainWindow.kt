package com.musicideas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.musicideas.ui.navigation.NavigationItem
import com.musicideas.ui.navigation.rememberNavController
import com.musicideas.ui.screens.record.RecordView
import com.musicideas.ui.screens.library.LibraryView
import com.musicideas.ui.screens.cloudstorage.CloudStorageView
import com.musicideas.ui.screens.settings.SettingsView
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.material.Icon
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import com.musicideas.ui.navigation.AppNavigation
import com.musicideas.ui.navigation.MainViewModel

@Composable
fun MainWindow(mainViewModel: MainViewModel) {
    MaterialTheme {
        AppNavigation(mainViewModel)
    }
}
