package com.mlmesa.savingdays.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Room entity representing user achievements/medals.
 */
@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Type of achievement
     */
    val type: AchievementType,
    
    /**
     * Whether this achievement has been unlocked
     */
    val isUnlocked: Boolean = false,
    
    /**
     * Timestamp when the achievement was unlocked (null if not unlocked)
     */
    val unlockedDate: LocalDateTime? = null
)

/**
 * Types of achievements available in the app
 */
enum class AchievementType {
    STREAK_7,        // 7 consecutive days
    DAYS_30,         // 30 days completed (not necessarily consecutive)
    DAYS_100,        // 100 days completed
    HALF_COMPLETE,   // 50% of challenge completed (182.5 days)
    COMPLETE         // All 365 days completed
}
