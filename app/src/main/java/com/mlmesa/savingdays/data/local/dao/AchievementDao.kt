package com.mlmesa.savingdays.data.local.dao

import androidx.room.*
import com.mlmesa.savingdays.data.local.entity.Achievement
import com.mlmesa.savingdays.data.local.entity.AchievementType
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Achievement entity
 */
@Dao
interface AchievementDao {
    
    /**
     * Get all achievements as a Flow
     */
    @Query("SELECT * FROM achievements ORDER BY id ASC")
    fun getAllAchievements(): Flow<List<Achievement>>
    
    /**
     * Get a specific achievement by type
     */
    @Query("SELECT * FROM achievements WHERE type = :type LIMIT 1")
    suspend fun getAchievementByType(type: AchievementType): Achievement?
    
    /**
     * Get all unlocked achievements
     */
    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 ORDER BY unlockedDate ASC")
    fun getUnlockedAchievements(): Flow<List<Achievement>>
    
    /**
     * Get count of unlocked achievements
     */
    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    fun getUnlockedCount(): Flow<Int>
    
    /**
     * Insert a new achievement
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(achievement: Achievement): Long
    
    /**
     * Insert multiple achievements
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievements(achievements: List<Achievement>)
    
    /**
     * Update an achievement
     */
    @Update
    suspend fun updateAchievement(achievement: Achievement)
    
    /**
     * Delete all achievements (for reset)
     */
    @Query("DELETE FROM achievements")
    suspend fun deleteAllAchievements()
}
