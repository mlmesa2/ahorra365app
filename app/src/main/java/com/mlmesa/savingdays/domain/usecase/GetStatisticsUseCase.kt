package com.mlmesa.savingdays.domain.usecase

import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.data.repository.ChallengeRepository
import com.mlmesa.savingdays.domain.model.Statistics
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Use case to calculate and return current statistics
 */
class GetStatisticsUseCase @Inject constructor(
    private val repository: ChallengeRepository,
    private val preferencesRepository: UserPreferencesRepository
) {
    
    /**
     * Get statistics as a Flow for reactive updates
     */
    operator fun invoke(year: Int = 2026, month: Int = 1): Flow<Statistics> {
        return combine(
            repository.getTotalSaved(),
            repository.getTotalSavedForMonth(year = year, month = month),
            repository.getCompletedCount(),
            preferencesRepository.userPreferencesFlow
        ) { totalSaved, monthSaved, completedCount, preferences ->
            val total = totalSaved ?: 0
            val remaining = Statistics.TOTAL_DAYS - completedCount
            val progressPercentage = (completedCount.toFloat() / Statistics.TOTAL_DAYS) * 100f
            
            Statistics(
                totalSaved = total,
                monthSaved = monthSaved ?: 0,
                daysCompleted = completedCount,
                daysRemaining = remaining,
                currentStreak = preferences.currentStreak,
                longestStreak = preferences.longestStreak,
                progressPercentage = progressPercentage
            )
        }
    }
    
    /**
     * Get statistics as a single snapshot (suspend function)
     */
    suspend fun getSnapshot(): Statistics {
        return invoke().first()
    }
}
