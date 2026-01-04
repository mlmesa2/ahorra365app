package com.mlmesa.savingdays.domain.model

import java.time.LocalDate

/**
 * Domain model for a daily challenge.
 * Separated from the entity for clean architecture.
 */
data class DailyChallengeModel(
    val id: Long,
    val dayNumber: Int,
    val amount: Int,
    val date: LocalDate,
    val isCompleted: Boolean,
    val completedDate: LocalDate?,
    val year: Int
)
