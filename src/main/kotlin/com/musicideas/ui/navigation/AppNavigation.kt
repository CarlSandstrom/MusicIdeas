package com.musicideas.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.musicideas.ui.screens.record.RecordView
import com.musicideas.ui.screens.library.LibraryView
import com.musicideas.ui.screens.settings.SettingsView
import com.musicideas.ui.screens.cloudstorage.CloudStorageView

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Record : Screen("record", "Record", Icons.Filled.Mic)
    data object Library : Screen("library", "Library", Icons.Filled.LibraryMusic)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
    data object CloudStorage : Screen("cloud", "Cloud Storage", Icons.Filled.Cloud)
}

@Composable
fun AppNavigation(mainViewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Record) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreen.title) }
            )
        },
        bottomBar = {
            BottomNavigation {
                listOf(Screen.Record, Screen.Library, Screen.Settings, Screen.CloudStorage).forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (currentScreen) {
                Screen.Record -> {
                    val recordViewModel = remember { mainViewModel.createRecordViewModel() }
                    RecordView(recordViewModel)
                }
                Screen.Library -> {
                    val libraryViewModel = remember { mainViewModel.createLibraryViewModel() }
                    LibraryView(libraryViewModel)
                }
                Screen.Settings -> {
                    val settingsViewModel = remember { mainViewModel.createSettingsViewModel() }
                    SettingsView(settingsViewModel)
                }
                Screen.CloudStorage -> {
                    val cloudStorageViewModel = remember { mainViewModel.createCloudStorageViewModel() }
                    CloudStorageView(cloudStorageViewModel)
                }
            }
        }
    }
}