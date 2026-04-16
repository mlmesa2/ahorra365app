package com.mlmesa.savingdays.ui.home

import android.app.Activity
import android.util.Log
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mlmesa.savingdays.data.local.entity.Achievement
import com.mlmesa.savingdays.data.local.entity.DailyChallenge
import com.mlmesa.savingdays.domain.model.Statistics
import com.mlmesa.savingdays.ui.components.ScreenTitle
import com.mlmesa.savingdays.ui.theme.Saving365Theme
import com.mlmesa.savingdays.util.DateUtils
import com.mlmesa.savingdays.util.NotificationPermissionRequest
import com.mlmesa.savingdays.data.model.CurrencyScale
import com.mlmesa.savingdays.R
import java.time.LocalDate

/**
 * Home screen showing today's challenge
 */
@OptIn(ExperimentalMaterial3Api::class,
    ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val todayChallenge by viewModel.todayChallenge.collectAsStateWithLifecycle()
    val statistics by viewModel.statistics.collectAsStateWithLifecycle()
    val motivationalMessageRes by viewModel.motivationalMessageRes.collectAsStateWithLifecycle()
    val currencySymbol by viewModel.currencySymbol.collectAsStateWithLifecycle()
    val currencyScale by viewModel.currencyScale.collectAsStateWithLifecycle()
    val notificationIsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val newAchievements by viewModel.newlyUnlockedAchievements.collectAsStateWithLifecycle()
    val shouldShowReview by viewModel.shouldShowReview.collectAsStateWithLifecycle()
    
    val context = LocalContext.current
    val activity = context as? Activity
    val inAppReviewManager = remember { viewModel.inAppReviewManager }

    // Trigger review flow when shouldShowReview is true
    LaunchedEffect(shouldShowReview) {
        if (shouldShowReview && activity != null) {
            inAppReviewManager.requestReviewFlow(activity) { success ->
                viewModel.reviewShown()
                if (!success) {
                    Log.d("HomeScreen", "Review flow failed or not available")
                }
            }
        } else {
            Log.d("HomeScreen", "Review flow not triggered shouldShowReview: $shouldShowReview, activity in null: ${activity == null}")
        }
    }

    HomeScreen(
        todayChallenge = todayChallenge,
        statistics = statistics,
        motivationalMessageRes = motivationalMessageRes,
        currencySymbol = currencySymbol,
        currencyScale = currencyScale,
        notificationIsEnabled = notificationIsEnabled,
        isLoading = isLoading,
        newAchievements = newAchievements,
        clearNewAchievements = viewModel::clearNewAchievements,
        toggleNotificationOnOff = viewModel::toggleNotificationOnOff,
        refresh = viewModel::refresh,
        completeChallenge = viewModel::completeChallenge
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    todayChallenge: DailyChallenge?,
    statistics: Statistics?,
    motivationalMessageRes: Int,
    currencySymbol: String,
    currencyScale: CurrencyScale,
    notificationIsEnabled: Boolean,
    isLoading: Boolean,
    newAchievements: List<Achievement>,
    clearNewAchievements: () -> Unit,
    toggleNotificationOnOff: (Boolean) -> Unit,
    refresh: () -> Unit,
    completeChallenge: () -> Unit
) {


    // Show achievement dialog if there are new achievements
    if (newAchievements.isNotEmpty()) {
        AchievementUnlockedDialog(
            achievements = newAchievements,
            onDismiss = { clearNewAchievements() }
        )
    }

    if (notificationIsEnabled) {
        NotificationPermissionRequest(
            onPermissionGranted = { granted ->
                if (granted) {
                    Log.d("MYTAG", "HomeScreen: permission granted")
                } else {
                    Log.d("MYTAG", "HomeScreen: permission denied")
                    toggleNotificationOnOff(false)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { ScreenTitle(title = stringResource(R.string.home_screen_title)) },
                actions = {
                    IconButton(onClick = { refresh() }) {
                        Icon(
                            Icons.Default.Refresh, 
                            contentDescription = stringResource(R.string.home_screen_refresh_content_description)
                        )
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
                        text = stringResource(motivationalMessageRes),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Today's challenge card
                    todayChallenge?.let { challenge ->
                        TodayChallengeCard(
                            challenge = challenge,
                            currencyScale = currencyScale,
                            onComplete = { completeChallenge() }
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
                                    text = stringResource(R.string.home_screen_no_challenge_available),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    // Statistics summary
                    statistics?.let { stats ->
                        StatisticsCard(
                            statistics = stats,
                            currencyScale = currencyScale
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodayChallengeCard(
    challenge: DailyChallenge,
    currencyScale: CurrencyScale,
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
                text = stringResource(R.string.home_screen_challenge_day_number, challenge.dayNumber),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Date
            Text(
                text = DateUtils.formatMedium(challenge.date),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            HorizontalDivider()
            
            // Amount (with currency scale applied)
            Text(
                text = currencyScale.formatAmount(challenge.amount),
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = stringResource(R.string.home_screen_daily_amount_label),
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
                        text = stringResource(R.string.home_screen_challenge_completed),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.home_screen_mark_as_completed))
                }
            }
        }
    }
}

@Composable
fun StatisticsCard(
    statistics: Statistics,
    currencyScale: CurrencyScale
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.home_screen_statistics_progress_title),
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
                    text = stringResource(
                        R.string.home_screen_statistics_progress_percentage, 
                        statistics.progressPercentage.toInt()
                    ),
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
                    label = stringResource(R.string.home_screen_statistics_total_saved),
                    value = currencyScale.formatAmount(statistics.totalSaved)
                )
                StatItem(
                    label = stringResource(R.string.home_screen_statistics_month_saved),
                    value = "${statistics.monthSaved}"
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = stringResource(R.string.home_screen_statistics_days_completed),
                    value = "${statistics.daysCompleted} ✅"
                )
                StatItem(
                    label = stringResource(R.string.home_screen_statistics_current_streak),
                    value = "${statistics.currentStreak} 🔥"
                )
                StatItem(
                    label = stringResource(R.string.home_screen_statistics_longest_streak),
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
    achievements: List<Achievement>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.home_screen_achievement_dialog_title)) },
        text = {
            Column {
                achievements.forEach { achievement ->
                    Text("🏆 ${achievement.type.name}")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.home_screen_achievement_dialog_confirm))
            }
        }
    )
}


@PreviewLightDark
@Composable
fun HomeScreenPreview() {
    Saving365Theme {
        HomeScreen(
            todayChallenge = DailyChallenge(
                dayNumber = 125,
                amount = 125,
                date = LocalDate.now(),
                isCompleted = false,
                year = 2026
            ),
            statistics = Statistics(
                totalSaved = 5000,
                monthSaved = 1200,
                totalToComplete = 66795,
                daysCompleted = 45,
                daysRemaining = 320,
                currentStreak = 5,
                longestStreak = 12,
                progressPercentage = 15f
            ),
            motivationalMessageRes = R.string.motivational_message_1,
            currencySymbol = "$",
            currencyScale = CurrencyScale.MEXICO,
            notificationIsEnabled = false,
            isLoading = false,
            newAchievements = emptyList(),
            clearNewAchievements = {},
            toggleNotificationOnOff = {},
            refresh = {},
            completeChallenge = {}
        )
    }
}

@Preview
@Composable
private fun TodayChallengeCardPreview() {
    Saving365Theme {
        TodayChallengeCard(
            challenge = DailyChallenge(
                dayNumber = 3,
                id = 3,
                amount = 365,
                date = LocalDate.now(),
                isCompleted = true,
                year = 2026,
                completedDate = null
            ),
            currencyScale = CurrencyScale.COLOMBIA,
            onComplete = {}
        )
    }
}

@Preview
@Composable
private fun StatisticCardPreview() {
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
    Saving365Theme {
        StatisticsCard(
            statistics = statistics,
            currencyScale = CurrencyScale.COLOMBIA
        )
    }
}