package com.mlmesa.savingdays.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mlmesa.savingdays.BuildConfig
import com.mlmesa.savingdays.ui.theme.Saving365Theme
import com.mlmesa.savingdays.util.Constants

/**
 * Settings screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.userPreferences.collectAsState()
    val statistics by viewModel.statistics.collectAsState()
    val showResetDialog by viewModel.showResetDialog.collectAsState()
    
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notifications section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Notificaciones",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Activar notificaciones")
                        Switch(
                            checked = preferences.notificationsEnabled,
                            onCheckedChange = { viewModel.toggleNotifications(it) }
                        )
                    }
                    
                    if (preferences.notificationsEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showTimePickerDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hora: ${String.format("%02d:%02d", preferences.notificationHour, preferences.notificationMinute)}")
                        }
                    }
                }
            }
            
            // Currency section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Moneda",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedButton(
                        onClick = { showCurrencyDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AttachMoney, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Símbolo: ${preferences.currencySymbol}")
                    }
                }
            }
            
            // Auto-reset section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Reinicio automático",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Reiniciar el 1 de enero")
                            Text(
                                text = "El reto se reiniciará automáticamente cada año",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = preferences.autoResetEnabled,
                            onCheckedChange = { viewModel.toggleAutoReset(it) }
                        )
                    }
                }
            }
            
            // Statistics section
            statistics?.let { stats ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Estadísticas",
                            style = MaterialTheme.typography.titleMedium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("Total ahorrado: ${preferences.currencySymbol}${stats.totalSaved}")
                        Text("Días completados: ${stats.daysCompleted}/${Constants.TOTAL_CHALLENGE_DAYS}")
                        Text("Progreso: ${stats.progressPercentage.toInt()}%")
                        Text("Racha actual: ${stats.currentStreak} días")
                        Text("Mejor racha: ${stats.longestStreak} días")
                    }
                }
            }
            
            // Reset button
            Button(
                onClick = { viewModel.showResetConfirmation() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.RestartAlt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reiniciar reto")
            }
            
            // About section
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Acerca de",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ahorro365 v${BuildConfig.VERSION_NAME}\nReto de ahorro anual gamificado",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
    
    // Currency selection dialog
    if (showCurrencyDialog) {
        CurrencySelectionDialog(
            currentCurrency = preferences.currencySymbol,
            onSelect = { currency ->
                viewModel.updateCurrency(currency)
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }
    
    // Time picker dialog
    if (showTimePickerDialog) {
        TimePickerDialog(
            initialHour = preferences.notificationHour,
            initialMinute = preferences.notificationMinute,
            onConfirm = { hour, minute ->
                viewModel.updateNotificationTime(hour, minute)
                showTimePickerDialog = false
            },
            onDismiss = { showTimePickerDialog = false }
        )
    }
    
    // Reset confirmation dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideResetConfirmation() },
            title = { Text("¿Reiniciar reto?") },
            text = { Text("Esto eliminará todo tu progreso actual y comenzarás un nuevo reto. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.resetChallenge() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Reiniciar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideResetConfirmation() }) {
                    Text("Cancelar")
                }
            }
        )
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
        SettingsScreen()
    }
}