package com.mlmesa.savingdays.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mlmesa.savingdays.ui.navigation.Screen

/**
 * Bottom navigation bar for the app
 */
@Composable
fun BottomNavigationBar(
    navController: NavController
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Calendar,
        BottomNavItem.Statistics,
        BottomNavItem.Achievements,
        BottomNavItem.Settings
    )
    
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.screen.route,
                onClick = {
                    navController.navigate(item.screen.route) {
                        // Pop up to the start destination to avoid building up a large stack
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

/**
 * Bottom navigation items
 */
sealed class BottomNavItem(
    val screen: Screen,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        screen = Screen.Home,
        title = "Inicio",
        icon = Icons.Default.Home
    )
    
    object Calendar : BottomNavItem(
        screen = Screen.Calendar,
        title = "Calendario",
        icon = Icons.Default.DateRange
    )

    object Statistics : BottomNavItem(
        screen = Screen.Statistics,
        title = "Estadísticas",
        icon = Icons.Default.BarChart
    )
    
    object Achievements : BottomNavItem(
        screen = Screen.Achievements,
        title = "Logros",
        icon = Icons.Default.Star
    )
    
    object Settings : BottomNavItem(
        screen = Screen.Settings,
        title = "Ajustes",
        icon = Icons.Default.Settings
    )
}
