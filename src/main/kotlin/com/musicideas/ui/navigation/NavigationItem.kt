package com.musicideas.ui.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import compose.icons.FeatherIcons  // We'll use Feather icons instead of Material icons
import compose.icons.feathericons.Mic
import compose.icons.feathericons.Music
import compose.icons.feathericons.Cloud
import compose.icons.feathericons.Settings

sealed class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Record : NavigationItem(
        route = "record",
        title = "Record",
        icon = FeatherIcons.Mic
    )

    object Library : NavigationItem(
        route = "library",
        title = "Library",
        icon = FeatherIcons.Music
    )

    object CloudStorage : NavigationItem(
        route = "cloud_storage",
        title = "Cloud Storage",
        icon = FeatherIcons.Cloud
    )

    object Settings : NavigationItem(
        route = "settings",
        title = "Settings",
        icon = FeatherIcons.Settings
    )
}