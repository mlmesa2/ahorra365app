package com.mlmesa.savingdays.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mlmesa.savingdays.ui.achievements.AchievementsScreen
import com.mlmesa.savingdays.ui.calendar.CalendarScreen
import com.mlmesa.savingdays.ui.home.HomeScreen
import com.mlmesa.savingdays.ui.settings.SettingsScreen

/**
 * Navigation graph for the app
 */
@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        
        composable(Screen.Calendar.route) {
            CalendarScreen()
        }
        
        composable(Screen.Achievements.route) {
            AchievementsScreen()
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
