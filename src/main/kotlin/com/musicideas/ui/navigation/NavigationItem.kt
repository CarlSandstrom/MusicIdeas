package com.musicideas.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector


sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Record : NavigationItem(
        route = "record",
        title = "Record",
        icon = Icons.Default.Mic
    )

    data object Library : NavigationItem(
        route = "library",
        title = "Library",
        icon = Icons.Default.LibraryMusic
    )

    data object CloudStorage : NavigationItem(
        route = "cloud_storage",
        title = "Cloud Storage",
        icon = Icons.Default.CloudQueue
    )

    data object Settings : NavigationItem(
        route = "settings",
        title = "Settings",
        icon = Icons.Default.Settings
    )
}