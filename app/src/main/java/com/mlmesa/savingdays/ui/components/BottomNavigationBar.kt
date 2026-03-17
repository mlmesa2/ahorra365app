package com.mlmesa.savingdays.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavKey
import com.mlmesa.savingdays.R
import com.mlmesa.savingdays.ui.navigation.Screen

/**
 * Bottom navigation bar for the app
 */

@Composable
fun BottomNavigationBar(
    selectedKey: NavKey,
    onSelectKey: (NavKey) -> Unit,
    modifier: Modifier = Modifier
) {

    NavigationBar(
        modifier = modifier
    ) {
        TOP_LEVEL_DESTINATION.forEach {(topLevelDestination, data) ->
            NavigationBarItem(
                selected = topLevelDestination == selectedKey,
                onClick = { onSelectKey(topLevelDestination) },
                icon = {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = "Icon " + stringResource(data.title)
                    )
                },
                label = {
                    Text(text = stringResource(data.title))
                }
            )
        }
    }
}

/**
 * Bottom navigation items
 */

data class BottomNavItem(
    val title: Int,
    val icon: ImageVector
)

val TOP_LEVEL_DESTINATION = mapOf(
    Screen.Home to BottomNavItem(
        title = R.string.navbar_name_home,
        icon = Icons.Default.Home
    ),

    Screen.Calendar to BottomNavItem(
        title = R.string.navbar_name_calendar,
        icon = Icons.Default.DateRange
    ),

    Screen.Statistics to BottomNavItem(
        title = R.string.navbar_name_statistics,
        icon = Icons.Default.BarChart
    ),

    Screen.Achievements to BottomNavItem(
        title = R.string.navbar_name_achievements,
        icon = Icons.Default.Star
    ),

    Screen.Settings to BottomNavItem(
        title = R.string.navbar_name_settings,
        icon = Icons.Default.Settings
    )
)
