package com.musicideas.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.musicideas.core.model.MusicIdea
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
    var tempoForSave by remember { mutableStateOf(0) }
    var musicIdeaToEdit by remember { mutableStateOf<MusicIdea?>(null) }
    var audioDataForEdit by remember { mutableStateOf<ByteArray?>(null) }

    val recordViewModel = remember { mainViewModel.createRecordViewModel() }
    val libraryViewModel = remember { mainViewModel.createLibraryViewModel() }
    val settingsViewModel = remember { mainViewModel.createSettingsViewModel() }
    val cloudStorageViewModel = remember { mainViewModel.createCloudStorageViewModel() }
    val saveViewModel = remember(recordingToSave) {
        recordingToSave?.let { audioData ->
            mainViewModel.createSaveViewModel(
                audioData = audioData,
                initialTempo = tempoForSave,
                onSaveComplete = {
                    libraryViewModel.reload()
                    currentScreen = Screen.Record
                    recordingToSave = null
                }
            )
        }
    }
    val editViewModel = remember(musicIdeaToEdit, audioDataForEdit) {
        val idea = musicIdeaToEdit
        val audio = audioDataForEdit
        if (idea != null && audio != null) {
            mainViewModel.createSaveViewModel(
                audioData = audio,
                existingMusicIdea = idea,
                onSaveComplete = {
                    libraryViewModel.reload()
                    currentScreen = Screen.Library
                    musicIdeaToEdit = null
                    audioDataForEdit = null
                }
            )
        } else null
    }

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
                    viewModel = recordViewModel,
                    onSave = { audioData, tempo ->
                        recordingToSave = audioData
                        tempoForSave = tempo
                        currentScreen = Screen.Save
                    }
                )
                Screen.Library -> LibraryView(
                    viewModel = libraryViewModel,
                    onEdit = { musicIdea ->
                        musicIdeaToEdit = musicIdea
                        currentScreen = Screen.Edit
                    }
                )
                Screen.Settings -> SettingsView(settingsViewModel)
                Screen.CloudStorage -> CloudStorageView(cloudStorageViewModel)
                Screen.Save -> saveViewModel?.let { SaveView(it) }
                Screen.Edit -> editViewModel?.let { SaveView(it) }
            }
        }
    }
}