package com.mlmesa.savingdays.ui.achievements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.mlmesa.savingdays.R
import com.mlmesa.savingdays.data.local.entity.Achievement
import com.mlmesa.savingdays.data.local.entity.AchievementType
import com.mlmesa.savingdays.ui.theme.Saving365Theme

/**
 * Achievements screen showing all unlockable achievements
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    viewModel: AchievementsViewModel = hiltViewModel()
) {
    val achievements by viewModel.achievements.collectAsState()
    val unlockedCount by viewModel.unlockedCount.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()

    AchievementsScreen(
        achievements = achievements,
        unlockedCount = unlockedCount,
        completedCount = completedCount,
        currentStreak = currentStreak,
        getAchievementTitleRes = viewModel::getAchievementTitleRes,
        getAchievementDescriptionRes = viewModel::getAchievementDescriptionRes,
        getAchievementIcon = viewModel::getAchievementIcon,
        getProgress = viewModel::getProgress
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    modifier: Modifier = Modifier,
    achievements: List<Achievement>,
    unlockedCount: Int,
    completedCount: Int,
    currentStreak: Int,
    getAchievementTitleRes: (AchievementType) -> Int,
    getAchievementDescriptionRes: (AchievementType) -> Int,
    getAchievementIcon: (AchievementType) -> String,
    getProgress: (AchievementType, Int, Int) -> Float
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.achievements_screen_title)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Summary card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.achievements_screen_summary_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(
                            R.string.achievements_screen_summary_count,
                            unlockedCount,
                            achievements.size
                        ),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            // Achievements grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(achievements) { achievement ->
                    AchievementCard(
                        achievement = achievement,
                        title = stringResource(getAchievementTitleRes(achievement.type)),
                        description = stringResource(getAchievementDescriptionRes(achievement.type)),
                        icon = getAchievementIcon(achievement.type),
                        progress = getProgress(achievement.type, completedCount, currentStreak)
                    )
                }
            }
        }
    }
}

@Composable
fun AchievementCard(
    achievement: Achievement,
    title: String,
    description: String,
    icon: String,
    progress: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon
            Text(
                text = icon,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(8.dp)
            )
            
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            
            // Description or progress
            if (achievement.isUnlocked) {
                Text(
                    text = stringResource(R.string.achievements_screen_unlocked_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(
                            R.string.achievements_screen_progress_percentage,
                            (progress * 100).toInt()
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ArchivementScreenPreview() {
    val mockAchievements = AchievementType.entries.map { type ->
        Achievement(
            type = type,
            isUnlocked = type == AchievementType.STREAK_7 || type == AchievementType.DAYS_30
        )
    }

    Saving365Theme {
        AchievementsScreen(
            achievements = mockAchievements,
            unlockedCount = 2,
            completedCount = 45,
            currentStreak = 10,
            getAchievementTitleRes = { type ->
                when (type) {
                    AchievementType.STREAK_7 -> R.string.achievements_screen_streak_7_title
                    AchievementType.DAYS_30 -> R.string.achievements_screen_days_30_title
                    AchievementType.DAYS_100 -> R.string.achievements_screen_days_100_title
                    AchievementType.HALF_COMPLETE -> R.string.achievements_screen_half_complete_title
                    AchievementType.COMPLETE -> R.string.achievements_screen_complete_title
                }
            },
            getAchievementDescriptionRes = { R.string.achievements_screen_streak_7_description },
            getAchievementIcon = { type ->
                when (type) {
                    AchievementType.STREAK_7 -> "🔥"
                    AchievementType.DAYS_30 -> "⭐"
                    AchievementType.DAYS_100 -> "💎"
                    AchievementType.HALF_COMPLETE -> "🏆"
                    AchievementType.COMPLETE -> "👑"
                }
            },
            getProgress = { type, completed, streak ->
                when (type) {
                    AchievementType.STREAK_7 -> (streak.toFloat() / 7f).coerceIn(0f, 1f)
                    AchievementType.DAYS_30 -> (completed.toFloat() / 30f).coerceIn(0f, 1f)
                    AchievementType.DAYS_100 -> (completed.toFloat() / 100f).coerceIn(0f, 1f)
                    AchievementType.HALF_COMPLETE -> (completed.toFloat() / 183f).coerceIn(0f, 1f)
                    AchievementType.COMPLETE -> (completed.toFloat() / 365f).coerceIn(0f, 1f)
                }
            }
        )
    }
}
