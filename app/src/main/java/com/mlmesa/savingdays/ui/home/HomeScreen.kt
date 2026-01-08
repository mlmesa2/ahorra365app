package com.mlmesa.savingdays.ui.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mlmesa.savingdays.domain.model.Statistics
import com.mlmesa.savingdays.ui.theme.Saving365Theme
import com.mlmesa.savingdays.util.DateUtils

/**
 * Home screen showing today's challenge
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val todayChallenge by viewModel.todayChallenge.collectAsStateWithLifecycle()
    val statistics by viewModel.statistics.collectAsStateWithLifecycle()
    val motivationalMessage by viewModel.motivationalMessage.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val newAchievements by viewModel.newlyUnlockedAchievements.collectAsStateWithLifecycle()
    
    // Show achievement dialog if there are new achievements
    if (newAchievements.isNotEmpty()) {
        AchievementUnlockedDialog(
            achievements = newAchievements,
            onDismiss = { viewModel.clearNewAchievements() }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ahorra365") },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Motivational message
                    Text(
                        text = motivationalMessage,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Today's challenge card
                    todayChallenge?.let { challenge ->
                        TodayChallengeCard(
                            challenge = challenge,
                            currencySymbol = currencySymbol,
                            onComplete = { viewModel.completeChallenge() }
                        )
                    } ?: run {
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No hay reto disponible para hoy",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    
                    // Statistics summary
                    statistics?.let { stats ->
                        StatisticsCard(
                            statistics = stats,
                            currencySymbol = currencySymbol
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodayChallengeCard(
    challenge: com.mlmesa.savingdays.data.local.entity.DailyChallenge,
    currencySymbol: String,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = if (challenge.isCompleted) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Day number
            Text(
                text = "Día ${challenge.dayNumber} de 365",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Date
            Text(
                text = DateUtils.formatMedium(challenge.date),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Divider()
            
            // Amount
            Text(
                text = "$currencySymbol${challenge.amount}",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Monto del día",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Complete button
            if (challenge.isCompleted) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "¡Completado!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Marcar como completado")
                }
            }
        }
    }
}

@Composable
fun StatisticsCard(
    statistics: Statistics,
    currencySymbol: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Progreso",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            // Progress bar
            Column {
                LinearProgressIndicator(
                    progress = { statistics.progressPercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${statistics.progressPercentage.toInt()}% completado",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            // Stats grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Total ahorrado",
                    value = "$currencySymbol${statistics.totalSaved}"
                )
                StatItem(
                    label = "Días completados",
                    value = "${statistics.daysCompleted}"
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Racha actual",
                    value = "${statistics.currentStreak} 🔥"
                )
                StatItem(
                    label = "Mejor racha",
                    value = "${statistics.longestStreak} 🏆"
                )
            }
        }
    }
}

@Composable
fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AchievementUnlockedDialog(
    achievements: List<com.mlmesa.savingdays.data.local.entity.Achievement>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¡Logro Desbloqueado!") },
        text = {
            Column {
                achievements.forEach { achievement ->
                    Text("🏆 ${achievement.type.name}")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("¡Genial!")
            }
        }
    )
}

@PreviewLightDark
@Composable
fun HomeScreenPreview() {
    Saving365Theme {
        HomeScreen()
    }
}