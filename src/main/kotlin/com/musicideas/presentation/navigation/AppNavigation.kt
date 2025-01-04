package com.musicideas.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.musicideas.domain.model.MusicIdea
import com.musicideas.presentation.screens.cloudstorage.CloudStorageView
import com.musicideas.presentation.screens.library.LibraryView
import com.musicideas.presentation.screens.record.RecordView
import com.musicideas.presentation.screens.save.SaveView
import com.musicideas.presentation.screens.settings.SettingsView

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Record : Screen("record", "Record", Icons.Filled.Mic)
    data object Library : Screen("library", "Library", Icons.Filled.LibraryMusic)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
    data object CloudStorage : Screen("cloud", "Cloud Storage", Icons.Filled.Cloud)
    data object Save : Screen("save", "Save Recording", Icons.Filled.Save)
    data object Edit : Screen("edit", "Edit Recording", Icons.Filled.Edit)
}

@Composable
fun AppNavigation(mainViewModel: MainViewModel) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Record) }
    var recordingToSave by remember { mutableStateOf<ByteArray?>(null) }
    var musicIdeaToEdit by remember { mutableStateOf<MusicIdea?>(null) }
    var audioDataForEdit by remember { mutableStateOf<ByteArray?>(null) }

    // Load audio data when musicIdeaToEdit changes
    LaunchedEffect(musicIdeaToEdit) {
        if (musicIdeaToEdit != null) {
            audioDataForEdit = musicIdeaToEdit?.audioDataProvider?.let { it() }
        } else {
            audioDataForEdit = null
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(currentScreen.title) }) },
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
                Screen.Record -> RecordView(
                    viewModel = mainViewModel.createRecordViewModel(),
                    onSave = { audioData ->
                        recordingToSave = audioData
                        currentScreen = Screen.Save
                    }
                )
                Screen.Library -> LibraryView(
                    viewModel = mainViewModel.createLibraryViewModel(),
                    onEdit = { musicIdea ->
                        musicIdeaToEdit = musicIdea
                        currentScreen = Screen.Edit
                    }
                )
                Screen.Settings -> SettingsView(mainViewModel.createSettingsViewModel())
                Screen.CloudStorage -> CloudStorageView(mainViewModel.createCloudStorageViewModel())
                Screen.Save -> {
                    recordingToSave?.let { audioData ->
                        SaveView(
                            mainViewModel.createSaveViewModel(
                                audioData = audioData,
                                onSaveComplete = {
                                    currentScreen = Screen.Record
                                    recordingToSave = null
                                }
                            )
                        )
                    }
                }
                Screen.Edit -> {
                    val idea = musicIdeaToEdit
                    val audio = audioDataForEdit
                    if (idea != null && audio != null) {
                        SaveView(
                            mainViewModel.createSaveViewModel(
                                audioData = audio,
                                existingMusicIdea = idea,
                                onSaveComplete = {
                                    currentScreen = Screen.Library
                                    musicIdeaToEdit = null
                                    audioDataForEdit = null
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}