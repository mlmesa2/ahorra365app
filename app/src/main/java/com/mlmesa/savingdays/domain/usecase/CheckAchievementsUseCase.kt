package com.mlmesa.savingdays.domain.usecase

import com.mlmesa.savingdays.data.local.entity.Achievement
import com.mlmesa.savingdays.data.local.entity.AchievementType
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.data.repository.ChallengeRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Use case to check and unlock achievements based on user progress
 */
class CheckAchievementsUseCase @Inject constructor(
    private val repository: ChallengeRepository,
    private val preferencesRepository: UserPreferencesRepository
) {
    
    /**
     * Check all achievements and unlock any that have been earned
     * @return List of newly unlocked achievements
     */
    suspend operator fun invoke(): List<Achievement> {
        val newlyUnlocked = mutableListOf<Achievement>()
        
        // Get current stats
        val completedCount = repository.getCompletedCount().first()
        val preferences = preferencesRepository.userPreferencesFlow.first()
        val currentStreak = preferences.currentStreak
        
        // Check each achievement type
        AchievementType.values().forEach { type ->
            val achievement = repository.getAchievementByType(type)
            
            if (achievement != null && !achievement.isUnlocked) {
                val shouldUnlock = when (type) {
                    AchievementType.STREAK_7 -> currentStreak >= 7
                    AchievementType.DAYS_30 -> completedCount >= 30
                    AchievementType.DAYS_100 -> completedCount >= 100
                    AchievementType.HALF_COMPLETE -> completedCount >= 183 // 50% of 365
                    AchievementType.COMPLETE -> completedCount >= 365
                }
                
                if (shouldUnlock) {
                    val unlockedAchievement = achievement.copy(
                        isUnlocked = true,
                        unlockedDate = LocalDateTime.now()
                    )
                    repository.updateAchievement(unlockedAchievement)
                    newlyUnlocked.add(unlockedAchievement)
                }
            }
        }
        
        return newlyUnlocked
    }
}
