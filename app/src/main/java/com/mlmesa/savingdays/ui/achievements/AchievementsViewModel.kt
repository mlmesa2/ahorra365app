package com.mlmesa.savingdays.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mlmesa.savingdays.R
import com.mlmesa.savingdays.data.local.entity.Achievement
import com.mlmesa.savingdays.data.local.entity.AchievementType
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.data.repository.ChallengeRepository
import com.mlmesa.savingdays.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

/**
 * ViewModel for the Achievements screen
 */
@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val repository: ChallengeRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    // All achievements
    val achievements: StateFlow<List<Achievement>> = repository.getAllAchievements()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Unlocked achievements count
    val unlockedCount: StateFlow<Int> = repository.getUnlockedAchievementsCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    // Current stats for progress display
    val completedCount: StateFlow<Int> = repository.getCompletedCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    val currentStreak: StateFlow<Int> = preferencesRepository.userPreferencesFlow
        .map { it.currentStreak }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
    
    /**
     * Get achievement title resource ID
     */
    fun getAchievementTitleRes(type: AchievementType): Int {
        return when (type) {
            AchievementType.STREAK_7 -> R.string.achievements_screen_streak_7_title
            AchievementType.DAYS_30 -> R.string.achievements_screen_days_30_title
            AchievementType.DAYS_100 -> R.string.achievements_screen_days_100_title
            AchievementType.HALF_COMPLETE -> R.string.achievements_screen_half_complete_title
            AchievementType.COMPLETE -> R.string.achievements_screen_complete_title
        }
    }
    
    /**
     * Get achievement description resource ID
     */
    fun getAchievementDescriptionRes(type: AchievementType): Int {
        return when (type) {
            AchievementType.STREAK_7 -> R.string.achievements_screen_streak_7_description
            AchievementType.DAYS_30 -> R.string.achievements_screen_days_30_description
            AchievementType.DAYS_100 -> R.string.achievements_screen_days_100_description
            AchievementType.HALF_COMPLETE -> R.string.achievements_screen_half_complete_description
            AchievementType.COMPLETE -> R.string.achievements_screen_complete_description
        }
    }
    
    /**
     * Get achievement icon emoji
     */
    fun getAchievementIcon(type: AchievementType): String {
        return when (type) {
            AchievementType.STREAK_7 -> "🔥"
            AchievementType.DAYS_30 -> "⭐"
            AchievementType.DAYS_100 -> "💎"
            AchievementType.HALF_COMPLETE -> "🏆"
            AchievementType.COMPLETE -> "👑"
        }
    }
    
    /**
     * Get progress toward achievement
     */
    fun getProgress(type: AchievementType, completed: Int, streak: Int): Float {
        val target = when (type) {
            AchievementType.STREAK_7 -> Constants.ACHIEVEMENT_STREAK_7
            AchievementType.DAYS_30 -> Constants.ACHIEVEMENT_DAYS_30
            AchievementType.DAYS_100 -> Constants.ACHIEVEMENT_DAYS_100
            AchievementType.HALF_COMPLETE -> Constants.ACHIEVEMENT_HALF_COMPLETE
            AchievementType.COMPLETE -> Constants.ACHIEVEMENT_COMPLETE
        }
        
        val current = when (type) {
            AchievementType.STREAK_7 -> streak
            else -> completed
        }
        
        return (current.toFloat() / target.toFloat()).coerceIn(0f, 1f)
    }
}
