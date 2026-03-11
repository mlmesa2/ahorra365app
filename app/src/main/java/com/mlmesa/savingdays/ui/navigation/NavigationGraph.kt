package com.mlmesa.savingdays.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mlmesa.savingdays.ui.achievements.AchievementsScreen
import com.mlmesa.savingdays.ui.calendar.CalendarScreen
import com.mlmesa.savingdays.ui.home.HomeScreen
import com.mlmesa.savingdays.ui.onboarding.OnboardingScreen
import com.mlmesa.savingdays.ui.settings.SettingsScreen
import com.mlmesa.savingdays.ui.statistics.StatisticsScreen

/**
 * Navigation graph for the app
 */
@Composable
fun NavigationGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: String = Screen.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onOnboardingComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen()
        }
        
        composable(Screen.Calendar.route) {
            CalendarScreen()
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen()
        }
        
        composable(Screen.Achievements.route) {
            AchievementsScreen()
        }
        
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
