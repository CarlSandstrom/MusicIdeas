package com.musicideas.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import com.musicideas.core.model.MusicIdea
import com.musicideas.data.audio.config.AudioFormatConfig
import com.musicideas.data.audio.encoding.Mp3Decoder
import com.musicideas.presentation.screens.library.AudioImport
import java.awt.Window
import org.koin.compose.getKoin
import com.musicideas.presentation.screens.library.LibraryView
import com.musicideas.presentation.screens.record.RecordView
import com.musicideas.presentation.screens.save.SaveView
import com.musicideas.presentation.screens.settings.SettingsView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.DnDConstants
import java.awt.dnd.DropTarget
import java.awt.dnd.DropTargetAdapter
import java.awt.dnd.DropTargetDropEvent
import java.awt.event.ContainerAdapter
import java.awt.event.ContainerEvent
import java.io.File

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    data object Record : Screen("record", "Record", Icons.Filled.Mic)
    data object Library : Screen("library", "Library", Icons.Filled.LibraryMusic)
    data object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
    data object Save : Screen("save", "Save Recording", Icons.Filled.Save)
    data object Edit : Screen("edit", "Edit Recording", Icons.Filled.Edit)
}

private data class PendingSave(val audioData: ByteArray, val tempo: Int, val name: String, val sampleRate: Int)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppNavigation(mainViewModel: MainViewModel, window: Window) {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Record) }
    var pendingSave by remember { mutableStateOf<PendingSave?>(null) }
    var musicIdeaToEdit by remember { mutableStateOf<MusicIdea?>(null) }
    var audioDataForEdit by remember { mutableStateOf<ByteArray?>(null) }

    val recordViewModel = remember { mainViewModel.createRecordViewModel() }
    val libraryViewModel = remember { mainViewModel.createLibraryViewModel() }
    val settingsViewModel = remember { mainViewModel.createSettingsViewModel() }
    val saveViewModel = remember(pendingSave) {
        pendingSave?.let { (audioData, tempo, name, sampleRate) ->
            mainViewModel.createSaveViewModel(
                audioData = audioData,
                initialTempo = tempo,
                initialName = name,
                initialSampleRate = sampleRate,
                onSaveComplete = {
                    libraryViewModel.reload()
                    currentScreen = Screen.Record
                    pendingSave = null
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

    val scope = rememberCoroutineScope()
    val koin = getKoin()
    val mp3Decoder = remember { koin.get<Mp3Decoder>() }
    LaunchedEffect(Unit) {
        val dropListener = object : DropTargetAdapter() {
            override fun drop(event: DropTargetDropEvent) {
                event.acceptDrop(DnDConstants.ACTION_COPY)
                @Suppress("UNCHECKED_CAST")
                val files = (event.transferable.getTransferData(DataFlavor.javaFileListFlavor) as List<*>)
                    .filterIsInstance<File>()
                if (files.isEmpty()) { event.dropComplete(false); return }

                if (currentScreen == Screen.Library) {
                    scope.launch {
                        val entries = withContext(Dispatchers.IO) {
                            files.map { file ->
                                val decoded = mp3Decoder.decode(file.readBytes())
                                AudioImport(file.nameWithoutExtension, decoded.pcmBytes, decoded.sampleRate)
                            }
                        }
                        libraryViewModel.importFiles(entries)
                    }
                } else {
                    val file = files.first()
                    scope.launch {
                        val decoded = withContext(Dispatchers.IO) { mp3Decoder.decode(file.readBytes()) }
                        pendingSave = PendingSave(decoded.pcmBytes, 120, file.nameWithoutExtension, decoded.sampleRate)
                        currentScreen = Screen.Save
                    }
                }
                event.dropComplete(true)
            }
        }

        fun java.awt.Component.installDropTarget() {
            dropTarget = DropTarget(this, DnDConstants.ACTION_COPY, dropListener)
            if (this is java.awt.Container) {
                addContainerListener(object : ContainerAdapter() {
                    override fun componentAdded(e: ContainerEvent) = e.child.installDropTarget()
                })
                components.forEach { it.installDropTarget() }
            }
        }

        window.installDropTarget()
    }

    val onBack: (() -> Unit)? = when (currentScreen) {
        Screen.Save -> ({
            pendingSave = null
            currentScreen = Screen.Record
        })
        Screen.Edit -> ({
            musicIdeaToEdit = null
            audioDataForEdit = null
            currentScreen = Screen.Library
        })
        else -> null
    }

    Box(
        modifier = Modifier.fillMaxSize().onPointerEvent(PointerEventType.Press) { event ->
            if (event.button == PointerButton.Back) onBack?.invoke()
        }
    ) {
        Scaffold(
            topBar = {
                var showFilterMenu by remember { mutableStateOf(false) }
                TopAppBar(
                    title = { Text(currentScreen.title) },
                    navigationIcon = onBack?.let { back ->
                        {
                            IconButton(onClick = back) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        if (currentScreen == Screen.Library) {
                            Box {
                                IconButton(onClick = { showFilterMenu = true }) {
                                    Icon(Icons.Filled.Tune, contentDescription = "Filter visibility")
                                }
                                DropdownMenu(
                                    expanded = showFilterMenu,
                                    onDismissRequest = { showFilterMenu = false }
                                ) {
                                    DropdownMenuItem(onClick = { settingsViewModel.onShowGenreFilterChanged(!settingsViewModel.showGenreFilter) }) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(checked = settingsViewModel.showGenreFilter, onCheckedChange = null)
                                            Text("Genre")
                                        }
                                    }
                                    DropdownMenuItem(onClick = { settingsViewModel.onShowInstrumentFilterChanged(!settingsViewModel.showInstrumentFilter) }) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(checked = settingsViewModel.showInstrumentFilter, onCheckedChange = null)
                                            Text("Instrument")
                                        }
                                    }
                                    DropdownMenuItem(onClick = { settingsViewModel.onShowIdeaTypeFilterChanged(!settingsViewModel.showIdeaTypeFilter) }) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(checked = settingsViewModel.showIdeaTypeFilter, onCheckedChange = null)
                                            Text("Idea Type")
                                        }
                                    }
                                    DropdownMenuItem(onClick = { settingsViewModel.onShowRatingFilterChanged(!settingsViewModel.showRatingFilter) }) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Checkbox(checked = settingsViewModel.showRatingFilter, onCheckedChange = null)
                                            Text("Rating")
                                        }
                                    }
                                }
                            }
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigation {
                    listOf(Screen.Record, Screen.Library, Screen.Settings).forEach { screen ->
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
                            pendingSave = PendingSave(audioData, tempo, "", AudioFormatConfig.format.sampleRate.toInt())
                            currentScreen = Screen.Save
                        }
                    )
                    Screen.Library -> LibraryView(
                        viewModel = libraryViewModel,
                        showGenreFilter = settingsViewModel.showGenreFilter,
                        showInstrumentFilter = settingsViewModel.showInstrumentFilter,
                        showIdeaTypeFilter = settingsViewModel.showIdeaTypeFilter,
                        showRatingFilter = settingsViewModel.showRatingFilter,
                        onEdit = { musicIdea ->
                            musicIdeaToEdit = musicIdea
                            currentScreen = Screen.Edit
                        }
                    )
                    Screen.Settings -> SettingsView(settingsViewModel)
                    Screen.Save -> saveViewModel?.let { SaveView(it) }
                    Screen.Edit -> editViewModel?.let { SaveView(it) }
                }
            }
        }
    }
}
