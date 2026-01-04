package com.mlmesa.savingdays.ui.navigation

/**
 * Navigation routes for the app
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Calendar : Screen("calendar")
    object Achievements : Screen("achievements")
    object Settings : Screen("settings")
}
