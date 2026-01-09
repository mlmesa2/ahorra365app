package com.mlmesa.savingdays.ui.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mlmesa.savingdays.R
import com.mlmesa.savingdays.data.local.entity.DailyChallenge
import com.mlmesa.savingdays.ui.theme.Saving365Theme
import com.mlmesa.savingdays.util.DateUtils
import java.time.LocalDate
import java.time.YearMonth

/**
 * Calendar screen showing all challenges
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val currentYearMonth by viewModel.currentYearMonth.collectAsStateWithLifecycle()
    val monthChallenges by viewModel.monthChallenges.collectAsStateWithLifecycle()
    val selectedChallenge by viewModel.selectedChallenge.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var isVisibleData by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text("${DateUtils.getMonthNames(context,currentYearMonth.monthValue)} ${currentYearMonth.year}")
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.previousMonth()
                        isVisibleData = false
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Mes anterior")
                    }
                    IconButton(onClick = {
                        viewModel.goToToday()
                        isVisibleData = true
                    }) {
                        Icon(Icons.Default.Today, contentDescription = "Hoy")
                    }
                    IconButton(onClick = {
                        viewModel.nextMonth()
                        isVisibleData = false
                    }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Siguiente mes")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Calendar grid
            CalendarGrid(
                yearMonth = currentYearMonth,
                challenges = monthChallenges,
                onDayClick = { date ->
                    viewModel.selectChallenge(date)
                    isVisibleData = true
                }
            )

            Spacer(modifier = Modifier.height(32.dp))


            selectedChallenge?.let { challenge ->

                ChallengeDetailsCard(
                    challenge = challenge,
                    isVisible = isVisibleData,
                    onComplete = { viewModel.completeChallenge(it) }
                )
            }

        }
    }
}

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    challenges: List<DailyChallenge>,
    onDayClick: (LocalDate) -> Unit
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 = Sunday
    
    Column {
        // Week day headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(
                stringResource(R.string.sunday_letter),
                stringResource(R.string.monday_letter),
                stringResource(R.string.tuesday_letter),
                stringResource(R.string.wednesday_letter),
                stringResource(R.string.thursday_letter),
                stringResource(R.string.friday_letter),
                stringResource(R.string.saturday_letter)
            ).forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Calendar days
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Empty cells before first day
            items(firstDayOfWeek) {
                Box(modifier = Modifier.aspectRatio(1f))
            }
            
            // Days of month
            items(daysInMonth) { dayIndex ->
                val day = dayIndex + 1
                val date = yearMonth.atDay(day)
                val challenge = challenges.find { it.date == date }
                
                DayCell(
                    day = day,
                    date = date,
                    challenge = challenge,
                    onClick = { onDayClick(date) }
                )
            }
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    date: LocalDate,
    challenge: DailyChallenge?,
    onClick: () -> Unit
) {
    val isToday = DateUtils.isToday(date)
    val isPast = DateUtils.isPast(date)
    val isFuture = DateUtils.isFuture(date)
    
    val backgroundColor = when {
        challenge?.isCompleted == true -> MaterialTheme.colorScheme.primaryContainer
        isPast && challenge != null && !challenge.isCompleted -> MaterialTheme.colorScheme.errorContainer
        isFuture -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(enabled = challenge != null) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
            
            if (challenge?.isCompleted == true) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ChallengeDetailsDialog(
    challenge: DailyChallenge,
    onDismiss: () -> Unit,
    onComplete: (DailyChallenge) -> Unit
) {
    val today = LocalDate.now()
    val isFuture = challenge.date.isAfter(today)
    val isPastOrToday = !isFuture
    val isPast = challenge.date.isBefore(today)
    val canComplete = isPastOrToday && !challenge.isCompleted
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Día ${challenge.dayNumber}") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Fecha: ${DateUtils.formatMedium(challenge.date)}")
                
                // Solo mostrar monto si el día ya pasó o es hoy
                if (isPastOrToday) {
                    Text(
                        text = "Monto: $${challenge.amount}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Monto: ???",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "El monto se revelará cuando llegue este día",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = if (challenge.isCompleted) "✓ Completado" else "Pendiente",
                    color = if (challenge.isCompleted) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (challenge.isCompleted) FontWeight.Bold else FontWeight.Normal
                )
                
                // Mensaje para días pasados no completados
                if (isPast && !challenge.isCompleted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Puedes marcar este día como completado aunque ya haya pasado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        },
        confirmButton = {
            if (canComplete) {
                Button(
                    onClick = {
                        onComplete(challenge)
                        onDismiss()
                    }
                ) {
                    Text("Marcar como completado")
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text("Cerrar")
                }
            }
        },
        dismissButton = if (canComplete) {
            {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        } else null
    )
}

@Composable
fun ChallengeDetailsCard(
    challenge: DailyChallenge,
    isVisible: Boolean = true,
    onComplete: (DailyChallenge) -> Unit
) {
    val today = LocalDate.now()
    val isFuture = challenge.date.isAfter(today)
    val isPastOrToday = !isFuture
    val isPast = challenge.date.isBefore(today)
    val canComplete = isPastOrToday && !challenge.isCompleted

    AnimatedVisibility(
        visible = isVisible
    ) {

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Día ${challenge.dayNumber}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text("Fecha: ${DateUtils.formatMedium(challenge.date)}")

                if (isPastOrToday) {
                    Text(
                        text = "Monto: $${challenge.amount}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Monto: ???",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "El monto se revelará cuando llegue este día",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

//                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (challenge.isCompleted) "✓ Completado" else "Pendiente",
                    color = if (challenge.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (challenge.isCompleted) FontWeight.Bold else FontWeight.Normal
                )

                // Mensaje para días pasados no completados
                if (isPast && !challenge.isCompleted) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Puedes marcar este día como completado aunque ya haya pasado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                if (canComplete) {
                    Button(
                        onClick = {
                            onComplete(challenge)
                        }) {
                        Text("Marcar como completado")
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CalendarCardPreview() {
    val challenge = DailyChallenge(
        id = 1,
        dayNumber = 1,
        amount = 456,
        date = LocalDate.now(),
        isCompleted = false,
        completedDate = null,
        year = 2026
    )
    Saving365Theme {
        ChallengeDetailsCard(
            challenge = challenge,
            onComplete = {}
        )
    }
}