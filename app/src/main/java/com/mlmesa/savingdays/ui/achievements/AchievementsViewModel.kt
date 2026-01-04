package com.mlmesa.savingdays.ui.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
     * Get achievement title
     */
    fun getAchievementTitle(type: AchievementType): String {
        return when (type) {
            AchievementType.STREAK_7 -> "Racha de 7 Días"
            AchievementType.DAYS_30 -> "30 Días Completados"
            AchievementType.DAYS_100 -> "100 Días Completados"
            AchievementType.HALF_COMPLETE -> "Mitad del Camino"
            AchievementType.COMPLETE -> "¡Reto Completado!"
        }
    }
    
    /**
     * Get achievement description
     */
    fun getAchievementDescription(type: AchievementType): String {
        return when (type) {
            AchievementType.STREAK_7 -> "Completa 7 días consecutivos"
            AchievementType.DAYS_30 -> "Completa 30 días del reto"
            AchievementType.DAYS_100 -> "Completa 100 días del reto"
            AchievementType.HALF_COMPLETE -> "Completa el 50% del reto (183 días)"
            AchievementType.COMPLETE -> "Completa los 365 días del reto"
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
