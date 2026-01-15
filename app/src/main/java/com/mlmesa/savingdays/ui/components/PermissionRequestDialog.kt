package com.mlmesa.savingdays.ui.components

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import androidx.core.net.toUri

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun PermissionRequestDialog(
    notificationPermissionState: PermissionState,
    onDismiss: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss(false) },
        title = { Text(text = "Permiso de notificación") },
        text = { Text(text = "Necesitamos permiso para mostrar notificaciones diariarias para recordarle el monto que debe ahorrar") },
        confirmButton = {
            TextButton(
                onClick = {
                    onDismiss(true)
                    notificationPermissionState.launchPermissionRequest()
                }
            ) {
                Text(text = "Permitir")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss(false) }) {
                Text(text = "Denegar")
            }
        }
    )
}


fun openAppNotificationSettings(context: Context, packageName: String) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
    intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        // Fallback para versiones antiguas o si no existe la acción específica
        val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        fallbackIntent.data = "package:$packageName".toUri()
        context.startActivity(fallbackIntent)
    }
}