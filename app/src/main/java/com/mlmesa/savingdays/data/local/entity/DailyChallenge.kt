package com.mlmesa.savingdays.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Room entity representing a daily savings challenge.
 * Each challenge has a unique amount (1-365) that the user must save.
 */
@Entity(tableName = "daily_challenges")
data class DailyChallenge(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Day number in the challenge sequence (1-365)
     */
    val dayNumber: Int,
    
    /**
     * Amount to save for this challenge (1-365)
     */
    val amount: Int,
    
    /**
     * Date when this challenge was assigned
     */
    val date: LocalDate,
    
    /**
     * Whether the user has completed this challenge
     */
    val isCompleted: Boolean = false,
    
    /**
     * Timestamp when the challenge was completed (null if not completed)
     */
    val completedDate: LocalDate? = null,
    
    /**
     * Year of the challenge (for multi-year support)
     */
    val year: Int
)
