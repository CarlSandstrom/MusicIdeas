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

@Composable
fun MainWindow() {
    val navController = rememberNavController()

    val items = listOf(
        NavigationItem.Record,
        NavigationItem.Library,
        NavigationItem.CloudStorage,
        NavigationItem.Settings
    )

    Row {
        // Side Navigation Panel
        Column(
            modifier = Modifier
                .width(200.dp)
                .fillMaxHeight()
                .background(Color(0xFFF0F0F0))
        ) {
            items.forEach { item ->
                NavigationItem(
                    item = item,
                    selected = navController.currentScreen.value == item.route,
                    onSelect = { navController.navigate(item.route) }
                )
            }
        }

        // Content Area
        Box(modifier = Modifier
            .weight(1f)
            .padding(16.dp)
        ) {
            when (navController.currentScreen.value) {
                NavigationItem.Record.route -> RecordView()
                NavigationItem.Library.route -> LibraryView()
                NavigationItem.CloudStorage.route -> CloudStorageView()
                NavigationItem.Settings.route -> SettingsView()
            }
        }
    }
}

@Composable
private fun NavigationItem(
    item: NavigationItem,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .background(if (selected) Color(0xFFE0E0E0) else Color.Transparent)
            .padding(16.dp)
    ) {
        Text(
            text = item.title,
            color = if (selected) Color(0xFF2196F3) else Color.Black,
            fontSize = 16.sp
        )
    }
}

// At the bottom of App.kt, after your App() composable
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Audio Recorder"
    ) {
        MainWindow()
    }
}