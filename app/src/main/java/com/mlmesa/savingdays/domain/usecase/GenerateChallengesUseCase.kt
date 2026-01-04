package com.mlmesa.savingdays.domain.usecase

import com.mlmesa.savingdays.data.local.entity.DailyChallenge
import com.mlmesa.savingdays.data.repository.ChallengeRepository
import java.time.LocalDate
import javax.inject.Inject

/**
 * Use case to generate 365 daily challenges with randomized amounts (1-365, no repeats).
 * This is called when starting a new challenge year.
 */
class GenerateChallengesUseCase @Inject constructor(
    private val repository: ChallengeRepository
) {
    
    /**
     * Generate and insert 365 challenges for the current year
     * Always generates from January 1st to December 31st
     * @param year The year for which to generate challenges
     * @return List of generated challenges
     */
    suspend operator fun invoke(year: Int = LocalDate.now().year): List<DailyChallenge> {
        // Always start from January 1st
        val startDate = LocalDate.of(year, 1, 1)
        
        // Create a list of amounts from 1 to 365 and shuffle them
        val amounts = (1..365).toList().shuffled()
        
        // Generate challenges for the entire year
        val challenges = amounts.mapIndexed { index, amount ->
            val challengeDate = startDate.plusDays(index.toLong())
            DailyChallenge(
                dayNumber = challengeDate.dayOfYear, // Use day of year (1-365)
                amount = amount,
                date = challengeDate,
                isCompleted = false,
                completedDate = null,
                year = year
            )
        }
        
        // Insert into database
        repository.insertChallenges(challenges)
        
        return challenges
    }
    
    /**
     * Generate challenges for a specific year
     */
    suspend fun generateForYear(year: Int): List<DailyChallenge> {
        return invoke(year)
    }
}
