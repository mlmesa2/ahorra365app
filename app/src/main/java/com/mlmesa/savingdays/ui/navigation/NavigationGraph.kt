package com.mlmesa.savingdays.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
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
    navigationState: NavigationState,
    navigator: Navigator
) {

    NavDisplay(
        modifier = modifier,
        onBack = navigator::goBack,
        entries = navigationState.toEntries(
            entryProvider = entryProvider {
                entry<Screen.Home> {
                    HomeScreen()
                }
                entry<Screen.Onboarding> {
                    OnboardingScreen(
                        onOnboardingComplete = {
                            navigator.navigate(Screen.Home)
                        }
                    )
                }
                entry<Screen.Calendar> {
                    CalendarScreen()
                }
                entry<Screen.Statistics> {
                    StatisticsScreen()
                }

                entry<Screen.Achievements> {
                    AchievementsScreen()
                }

                entry<Screen.Settings> {
                    SettingsScreen()
                }
            }
        )
    )
}
