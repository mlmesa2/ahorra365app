package com.mlmesa.savingdays.domain.usecase

import com.mlmesa.savingdays.data.local.entity.DailyChallenge
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.data.repository.ChallengeRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

/**
 * Use case to mark a challenge as completed and update streaks
 */
class CompleteChallengeUseCase @Inject constructor(
    private val repository: ChallengeRepository,
    private val preferencesRepository: UserPreferencesRepository
) {
    
    /**
     * Complete a challenge and update streak
     */
    suspend operator fun invoke(challenge: DailyChallenge) {
        // Mark challenge as completed
        repository.completeChallenge(challenge)
        
        // Update streak
        updateStreak(challenge.date)
    }
    
    /**
     * Update the user's streak based on completion date
     */
    private suspend fun updateStreak(completionDate: LocalDate) {
        val preferences = preferencesRepository.userPreferencesFlow.first()
        val lastCompletedDate = preferences.lastCompletedDate
        
        val newStreak = if (lastCompletedDate == null) {
            // First completion
            1
        } else {
            val daysBetween = ChronoUnit.DAYS.between(lastCompletedDate, completionDate)
            when {
                daysBetween == 1L -> preferences.currentStreak + 1 // Consecutive day
                daysBetween == 0L -> preferences.currentStreak // Same day (shouldn't happen)
                else -> 1 // Streak broken, start over
            }
        }
        
        // Update preferences
        preferencesRepository.setCurrentStreak(newStreak)
        preferencesRepository.setLastCompletedDate(completionDate)
    }
}
