package com.mlmesa.savingdays.domain.model

/**
 * Domain model representing app statistics
 */
data class Statistics(
    val totalSaved: Int,
    val monthSaved: Int,
    val daysCompleted: Int,
    val daysRemaining: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val progressPercentage: Float
) {
    companion object {
        const val TOTAL_DAYS = 365
    }
}
