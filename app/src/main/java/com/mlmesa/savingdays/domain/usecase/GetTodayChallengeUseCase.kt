package com.mlmesa.savingdays.domain.usecase

import com.mlmesa.savingdays.data.local.entity.DailyChallenge
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.data.repository.ChallengeRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case to get today's challenge.
 * If today's challenge doesn't exist, it will be created.
 */
class GetTodayChallengeUseCase @Inject constructor(
    private val repository: ChallengeRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val generateChallengesUseCase: GenerateChallengesUseCase
) {
    
    /**
     * Get today's challenge, creating it if necessary
     */
    suspend operator fun invoke(): DailyChallenge? {
        val today = LocalDate.now()
        
        // Try to get today's challenge
        var todayChallenge = repository.getChallengeByDate(today)
        
        // If it doesn't exist, check if we need to initialize challenges
        if (todayChallenge == null) {
            val preferences = preferencesRepository.userPreferencesFlow.first()
            
            // Check if this is the first time or if we need to auto-reset
            val shouldInitialize = preferences.challengeStartDate == null ||
                    (preferences.autoResetEnabled && isNewYear(preferences.challengeStartDate))
            
            if (shouldInitialize) {
                // Delete old challenges if auto-reset
                if (preferences.autoResetEnabled && preferences.challengeStartDate != null) {
                    repository.deleteAllChallenges()
                    repository.deleteAllAchievements()
                    repository.initializeAchievements()
                    preferencesRepository.resetPreferences()
                }
                
                // Generate new challenges for the entire year (always from Jan 1)
                val currentYear = today.year
                generateChallengesUseCase.invoke(currentYear)
                
                // Update start date to January 1st of current year
                val startDate = LocalDate.of(currentYear, 1, 1)
                preferencesRepository.setChallengeStartDate(startDate)
                
                // Get today's challenge again
                todayChallenge = repository.getChallengeByDate(today)
            }
        }
        
        return todayChallenge
    }
    
    /**
     * Check if it's a new year compared to the challenge start date
     */
    private fun isNewYear(challengeStartDate: LocalDate?): Boolean {
        if (challengeStartDate == null) return false
        val today = LocalDate.now()
        return today.year > challengeStartDate.year && today.monthValue == 1 && today.dayOfMonth == 1
    }
}
