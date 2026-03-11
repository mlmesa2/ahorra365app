package com.mlmesa.savingdays.ui.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mlmesa.savingdays.BuildConfig
import com.mlmesa.savingdays.R
import com.mlmesa.savingdays.data.local.preferences.UserPreferences
import com.mlmesa.savingdays.domain.model.Statistics
import com.mlmesa.savingdays.ui.components.ScreenTitle
import com.mlmesa.savingdays.ui.components.SettingsItem
import com.mlmesa.savingdays.ui.components.SettingsItemSwitch
import com.mlmesa.savingdays.ui.components.SettingsSection
import com.mlmesa.savingdays.ui.components.openAppNotificationSettings
import com.mlmesa.savingdays.ui.theme.Saving365Theme
import com.mlmesa.savingdays.util.Constants
import com.mlmesa.savingdays.util.NotificationPermissionRequest
import com.mlmesa.savingdays.util.launchChromeTab
import com.mlmesa.savingdays.data.model.CurrencyScale
import java.util.Locale

/**
 * Settings screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val statistics by viewModel.statistics.collectAsState()
    val showResetDialog by viewModel.showResetDialog.collectAsState()

    SettingsScreen(
        preferences = preferences,
        statistics = statistics,
        showResetDialog = showResetDialog,
        toggleNotifications = viewModel::toggleNotifications,
        toggleAutoReset = viewModel::toggleAutoReset,
        showResetConfirmation = { viewModel.showResetConfirmation() },
        updateNotificationTime = viewModel::updateNotificationTime,
        updateCurrency = viewModel::updateCurrency,
        updateCurrencyScale = viewModel::updateCurrencyScale,
        hideResetConfirmation = viewModel::hideResetConfirmation,
        resetChallenge = viewModel::resetChallenge,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    preferences: UserPreferences,
    statistics: Statistics?,
    showResetDialog: Boolean,
    toggleNotifications: (Boolean) -> Unit,
    toggleAutoReset: (Boolean) -> Unit,
    showResetConfirmation: () -> Unit,
    updateNotificationTime: (Int, Int) -> Unit,
    updateCurrency: (String) -> Unit,
    updateCurrencyScale: (CurrencyScale) -> Unit,
    hideResetConfirmation: () -> Unit,
    resetChallenge: () -> Unit,
) {

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showCountryDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val url = stringResource(R.string.pp_url)
    val barColor = MaterialTheme.colorScheme.primary.toArgb()

    var triggerPermissionCheck by remember { mutableStateOf(false) }
    var showForceDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { ScreenTitle(title = "Configuración") }
            )
        }
    ) { paddingValues ->

        if (triggerPermissionCheck) {
            NotificationPermissionRequest(
                onPermissionGranted = { granted ->
                    if (granted) {
                        Log.d("MYTAG", "SettingsScreen: granted")
                    } else{
                        Log.d("MYTAG", "SettingsScreen: not granted")
                        showForceDialog = true
                    }
                    toggleNotifications(granted)
                    triggerPermissionCheck = false
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Notifications section

            SettingsSection(title = "Notificaciones") {
                SettingsItemSwitch(
                    icon = if (preferences.notificationsEnabled) Icons.Default.AlarmOn else Icons.Default.AlarmOff,
                    title = "Activar notificaciones",
                    subtitle = "Notificaciones de nuevos retos",
                    onClick = null,
                    isSwitchChecked = preferences.notificationsEnabled,
                    onCheckedChange = { isChecked ->
                        if (isChecked) {
                            triggerPermissionCheck = true
                        } else {
                            toggleNotifications(false)
                        }
                    }
                )
                SettingsItem(
                    icon = Icons.Default.Notifications,
                    title = "Hora de notificación",
                    subtitle = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        preferences.notificationHour,
                        preferences.notificationMinute
                    ),
                    enabled = preferences.notificationsEnabled,
                    onClick = { showTimePickerDialog = true }
                )
            }

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // Currency section
            SettingsSection(title = "País / Moneda") {
                SettingsItem(
                    icon = Icons.Default.AttachMoney,
                    title = "País",
                    subtitle = stringResource(preferences.currencyScale.displayName),
                    onClick = { showCountryDialog = true }
                )
            }
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // Auto-reset section
            SettingsSection(title = "Reinicio automático") {
                SettingsItemSwitch(
                    icon = Icons.Default.RestartAlt,
                    title = "Reiniciar el 1 de enero",
                    subtitle = "El reto se reiniciará automáticamente cada año",
                    onClick = null,
                    isSwitchChecked = preferences.autoResetEnabled,
                    onCheckedChange = { toggleAutoReset(it) }
                )
            }


            // Statistics section
            statistics?.let { stats ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Estadísticas",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Total ahorrado: ${preferences.currencyScale.formatAmount(stats.totalSaved)}")
                        Text("Días completados: ${stats.daysCompleted}/${Constants.TOTAL_CHALLENGE_DAYS}")
                        Text("Progreso: ${stats.progressPercentage.toInt()}%")
                        Text("Racha actual: ${stats.currentStreak} días")
                        Text("Mejor racha: ${stats.longestStreak} días")
                    }
                }
            }

            // Reset button
            Button(
                onClick = { showResetConfirmation() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reiniciar reto ahora")
            }
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // About section
            SettingsSection(title = "Acerca de") {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = "Version",
                    subtitle = buildString {
                        append(stringResource(R.string.app_name))
                        append(" ")
                        append(BuildConfig.VERSION_NAME)
                    },
                    onClick = {
                        val packageName = context.packageName
                        try {
                            val storeIntent = Intent(
                                Intent.ACTION_VIEW,
                                "market://details?id=$packageName".toUri()
                            )
                            context.startActivity(storeIntent)
                        } catch (e: ActivityNotFoundException) {
                            val webIntent = Intent(
                                Intent.ACTION_VIEW,
                                "https://play.google.com/store/apps/details?id=$packageName".toUri()
                            )
                            // Verificamos que haya al menos un navegador para evitar otro crash
                            if (webIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(webIntent)
                            } else {
                                // Si tampoco hay navegador, informamos al usuario
                                Toast.makeText(
                                    context,
                                    "No se encontró un navegador para abrir la aplicación.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )

                SettingsItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Políticas de privacidad",
                    subtitle = url,
                    onClick = {
                        launchChromeTab(context = context, url = url, toolbarColor = barColor)
                    }
                )
            }
        }
    }

    // Currency selection dialog (legacy, kept for backwards compatibility)
    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            currentCurrency = preferences.currencySymbol,
            onSelect = { currency ->
                updateCurrency(currency)
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }

    // Country/Scale selection dialog (new)
    if (showCountryDialog) {
        CountryScaleSelectionDialog(
            daysCompleted = statistics?.daysCompleted,
            currentScale = preferences.currencyScale,
            onSelect = { scale ->
                updateCurrencyScale(scale)
                showCountryDialog = false
            },
            onDismiss = { showCountryDialog = false }
        )
    }

    // Time picker dialog
    if (showTimePickerDialog) {
        TimePickerDialog(
            initialHour = preferences.notificationHour,
            initialMinute = preferences.notificationMinute,
            onConfirm = { hour, minute ->
                updateNotificationTime(hour, minute)
                showTimePickerDialog = false
            },
            onDismiss = { showTimePickerDialog = false }
        )
    }

    // Reset confirmation dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { hideResetConfirmation() },
            title = { Text("¿Reiniciar reto?") },
            text = { Text("Esto eliminará todo tu progreso actual y comenzarás un nuevo reto. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = { resetChallenge() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reiniciar")
                }
            },
            dismissButton = {
                TextButton(onClick = { hideResetConfirmation() }) {
                    Text("Cancelar")
                }
            }
        )
    }
    if (showForceDialog) {
        AnimatedVisibility(
            visible = true
        ) {
            AlertDialog(
                onDismissRequest = { showForceDialog = false },
                title = { Text("Permiso de notificación") },
                text = { Text("Para otorgar el permiso de notificación es necesario ir a los ajustes de la app")},
                confirmButton = {
                    TextButton(onClick = {
                        openAppNotificationSettings(context, context.packageName)
                        showForceDialog = false
                    }) {
                        Text("Ir a ajustes")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showForceDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun CurrencySelectionDialog(
    currentCurrency: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var customCurrency by remember { mutableStateOf("") }
    var showCustomInput by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar moneda") },
        text = {
            Column {
                listOf("$", "€", "MXN", "£", "¥").forEach { currency ->
                    TextButton(
                        onClick = { onSelect(currency) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(currency)
                    }
                }

                if (showCustomInput) {
                    OutlinedTextField(
                        value = customCurrency,
                        onValueChange = { customCurrency = it },
                        label = { Text("Personalizado") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextButton(
                        onClick = {
                            if (customCurrency.isNotBlank()) {
                                onSelect(customCurrency)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Confirmar")
                    }
                } else {
                    TextButton(
                        onClick = { showCustomInput = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Personalizado...")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun CountryScaleSelectionDialog(
    daysCompleted: Int?,
    currentScale: CurrencyScale,
    onSelect: (CurrencyScale) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar país") },
        text = {
            Column {
                CurrencyScale.getAllScales().forEach { scale ->
                    TextButton(
                        onClick = { onSelect(scale) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (scale == currentScale) "✓ ${stringResource(scale.displayName)}" else stringResource(scale.displayName),
                            color = if (scale == currentScale) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                // Check if user has data (completed days > 0)
                if ((daysCompleted ?: 0) > 0) {
                    Text(
                        text = "Si cambias de país, los montos se ajustarán a la nueva moneda. Como ya tienes días completados, el resultado final del monto ahorrado podría variar del original mostrado.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Justify
                    )


                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar hora") },
        text = {
            TimePicker(state = timePickerState)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(timePickerState.hour, timePickerState.minute)
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Preview
@Composable
private fun SettingPreview() {
    Saving365Theme {
        val statistics = Statistics(
            totalSaved = 1000,
            monthSaved = 300,
            totalToComplete = 36500,
            daysCompleted = 1,
            daysRemaining = 365,
            currentStreak = 1,
            longestStreak = 1,
            progressPercentage = 100f,

            )
        SettingsScreen(
            preferences = UserPreferences(),
            statistics = statistics,
            showResetDialog = false,
            toggleNotifications = {},
            toggleAutoReset = {},
            showResetConfirmation = {},
            updateNotificationTime = { _, _ -> },
            updateCurrency = {},
            updateCurrencyScale = {},
            hideResetConfirmation = {},
            resetChallenge = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun CountryScaleSelectionDialogPreview() {
    Saving365Theme {
        CountryScaleSelectionDialog(
            daysCompleted = 1,
            currentScale = CurrencyScale.ARGENTINA,
            onSelect = {},
            onDismiss = {}
        )
    }
}