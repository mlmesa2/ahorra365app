package com.mlmesa.savingdays.util

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.mlmesa.savingdays.ui.components.PermissionRequestDialog

fun launchChromeTab(context: Context, url: String, @ColorInt toolbarColor: Int) {
    val customTabBarColor = CustomTabColorSchemeParams.Builder().setToolbarColor(toolbarColor).build()
    val customTabIntent = CustomTabsIntent.Builder().setDefaultColorSchemeParams(customTabBarColor).build()
    customTabIntent.launchUrl(context, url.toUri())
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun NotificationPermissionRequest(onPermissionGranted: (Boolean) -> Unit = {}) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val notificationPermissionState = rememberPermissionState(
            permission = Manifest.permission.POST_NOTIFICATIONS,
            onPermissionResult = {isGranted ->
                onPermissionGranted(isGranted)
            }
        )

        var showRationaleDialog by remember { mutableStateOf(false) }
        LaunchedEffect(notificationPermissionState.status.isGranted) {
            if (notificationPermissionState.status.isGranted) {
                onPermissionGranted(true)
            }
        }
        LaunchedEffect(key1 = Unit) {
            if (!notificationPermissionState.status.isGranted) {
                showRationaleDialog = true
            } else
                onPermissionGranted(true)
        }
        if (showRationaleDialog) {
            PermissionRequestDialog(
                notificationPermissionState = notificationPermissionState,
                onDismiss = {
                    onPermissionGranted(it)
                    showRationaleDialog = false
                }
            )
        }
    } else
        onPermissionGranted(true)
}