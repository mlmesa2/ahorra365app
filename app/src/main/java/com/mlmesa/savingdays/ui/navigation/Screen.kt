package com.mlmesa.savingdays.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * Navigation routes for the app
 */
@Serializable
sealed interface Screen : NavKey {

    @Serializable
    data object Onboarding : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object Calendar : Screen

    @Serializable
    data object Statistics : Screen

    @Serializable
    data object Achievements : Screen

    @Serializable
    data object Settings : Screen
}
