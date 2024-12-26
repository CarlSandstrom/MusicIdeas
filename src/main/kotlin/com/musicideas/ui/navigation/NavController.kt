package com.musicideas.ui.navigation

// ui/navigation/NavController.kt
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class NavController {
    var currentScreen: MutableState<String> = mutableStateOf(NavigationItem.Record.route)
    private var backStackScreens: MutableList<String> = mutableListOf()

    fun navigate(route: String) {
        if (route != currentScreen.value) {
            backStackScreens.add(currentScreen.value)
            currentScreen.value = route
        }
    }

    fun navigateBack() {
        if (backStackScreens.isNotEmpty()) {
            currentScreen.value = backStackScreens.last()
            backStackScreens.removeLast()
        }
    }
}

@Composable
fun rememberNavController(): NavController = remember { NavController() }