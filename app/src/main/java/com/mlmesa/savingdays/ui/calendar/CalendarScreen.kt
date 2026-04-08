package com.mlmesa.savingdays.ui.calendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.mlmesa.savingdays.data.model.CurrencyScale
import com.mlmesa.savingdays.ui.components.ScreenTitle
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
    val currencyScale by viewModel.currencyScale.collectAsStateWithLifecycle()

    CalendarScreen(
        currentYearMonth = currentYearMonth,
        monthChallenges = monthChallenges,
        selectedChallenge = selectedChallenge,
        currencyScale = currencyScale,
        previousMonth = viewModel::previousMonth,
        nextMonth = viewModel::nextMonth,
        goToToday = viewModel::goToToday,
        selectChallenge = viewModel::selectChallenge,
        completeChallenge = viewModel::completeChallenge
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    modifier: Modifier = Modifier,
    currentYearMonth: YearMonth,
    monthChallenges: List<DailyChallenge>,
    selectedChallenge: DailyChallenge?,
    currencyScale: CurrencyScale,
    previousMonth: () -> Unit,
    nextMonth: () -> Unit,
    goToToday: () -> Unit,
    selectChallenge: (LocalDate) -> Unit,
    completeChallenge: (DailyChallenge) -> Unit
) {
    var isVisibleData by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    ScreenTitle(title = "${DateUtils.getMonthNames(context,currentYearMonth.monthValue)} ${currentYearMonth.year}")
                },
                actions = {
                    IconButton(onClick = {
                        previousMonth()
                        isVisibleData = false
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.calendar_screen_previous_month))
                    }
                    IconButton(onClick = {
                        goToToday()
                        isVisibleData = true
                    }) {
                        Icon(Icons.Default.Today, contentDescription = stringResource(R.string.calendar_screen_today))
                    }
                    IconButton(onClick = {
                        nextMonth()
                        isVisibleData = false
                    }) {
                        Icon(Icons.Default.ArrowForward, contentDescription = stringResource(R.string.calendar_screen_next_month))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            // Calendar grid
            CalendarGrid(
                yearMonth = currentYearMonth,
                challenges = monthChallenges,
                onDayClick = { date ->
                    selectChallenge(date)
                    isVisibleData = true
                }
            )

            Spacer(modifier = Modifier.height(32.dp))


            selectedChallenge?.let { challenge ->

                ChallengeDetailsCard(
                    challenge = challenge,
                    currencyScale = currencyScale,
                    isVisible = isVisibleData,
                    onComplete = { completeChallenge(it) }
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
            .then(
                if (isToday) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                else Modifier
            )
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
    currencyScale: CurrencyScale,
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
        title = { Text(stringResource(R.string.calendar_screen_day_number, challenge.dayNumber)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(stringResource(R.string.calendar_screen_date_label, DateUtils.formatMedium(challenge.date)))
                
                // Solo mostrar monto si el día ya pasó o es hoy
                if (isPastOrToday) {
                    Text(
                        text = stringResource(R.string.calendar_screen_amount_label, currencyScale.formatAmount(challenge.amount)),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = stringResource(R.string.calendar_screen_amount_hidden),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.calendar_screen_amount_reveal_message),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = if (challenge.isCompleted) 
                        stringResource(R.string.calendar_screen_status_completed) 
                    else 
                        stringResource(R.string.calendar_screen_status_pending),
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
                        text = stringResource(R.string.calendar_screen_past_completion_message),
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
                    Text(stringResource(R.string.calendar_screen_mark_completed))
                }
            } else {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.calendar_screen_close))
                }
            }
        },
        dismissButton = if (canComplete) {
            {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.calendar_screen_cancel))
                }
            }
        } else null
    )
}

@Composable
fun ChallengeDetailsCard(
    challenge: DailyChallenge,
    currencyScale: CurrencyScale,
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
                    text = stringResource(R.string.calendar_screen_day_number, challenge.dayNumber),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(stringResource(R.string.calendar_screen_date_label, DateUtils.formatMedium(challenge.date)))

                if (isPastOrToday) {
                    Text(
                        text = stringResource(R.string.calendar_screen_amount_label, currencyScale.formatAmount(challenge.amount)),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = stringResource(R.string.calendar_screen_amount_hidden),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.calendar_screen_amount_reveal_message),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

//                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (challenge.isCompleted) 
                        stringResource(R.string.calendar_screen_status_completed) 
                    else 
                        stringResource(R.string.calendar_screen_status_pending),
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
                        text = stringResource(R.string.calendar_screen_past_completion_message),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                if (canComplete) {
                    Button(
                        onClick = {
                            onComplete(challenge)
                        }) {
                        Text(stringResource(R.string.calendar_screen_mark_completed))
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Preview(locale = "es")
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
            currencyScale = CurrencyScale.MEXICO,
            onComplete = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun DayCellPreview() {
    Saving365Theme {
        val challenge = DailyChallenge(
            id = 1,
            dayNumber = 1,
            amount = 456,
            date = LocalDate.now(),
            isCompleted = true,
            completedDate = null,
            year = 2026
        )
        DayCell(
            day = 23,
            date = LocalDate.now(),
            challenge = challenge
        ) { }
    }
}

@Preview
@Composable
private fun CalendarScreenPreview() {

    Saving365Theme {
        CalendarScreen(
            currentYearMonth = YearMonth.now(),
            monthChallenges = emptyList(),
            selectedChallenge = null,
            currencyScale = CurrencyScale.GENERIC,
            previousMonth = {},
            nextMonth = {},
            goToToday = {},
            selectChallenge = {},
            completeChallenge = {},
        )
    }

}