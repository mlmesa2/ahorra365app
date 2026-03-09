package com.mlmesa.savingdays.data.local.dao

import androidx.room.*
import com.mlmesa.savingdays.data.local.entity.DailyChallenge
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for DailyChallenge entity
 */
@Dao
interface DailyChallengeDao {
    
    /**
     * Get all challenges as a Flow for reactive updates
     */
    @Query("SELECT * FROM daily_challenges ORDER BY date ASC")
    fun getAllChallenges(): Flow<List<DailyChallenge>>
    
    /**
     * Get a specific challenge by date
     */
    @Query("SELECT * FROM daily_challenges WHERE date = :date LIMIT 1")
    suspend fun getChallengeByDate(date: LocalDate): DailyChallenge?

    /**
     * Get a specific challenge by date as a Flow
     */
    @Query("SELECT * FROM daily_challenges WHERE date = :date LIMIT 1")
    fun getChallengeByDateFlow(date: LocalDate): Flow<DailyChallenge?>
    
    /**
     * Get all completed challenges
     */
    @Query("SELECT * FROM daily_challenges WHERE isCompleted = 1 ORDER BY date ASC")
    fun getCompletedChallenges(): Flow<List<DailyChallenge>>
    
    /**
     * Get all challenges for a specific year
     */
    @Query("SELECT * FROM daily_challenges WHERE year = :year ORDER BY date ASC")
    fun getChallengesForYear(year: Int): Flow<List<DailyChallenge>>
    
    /**
     * Get challenges for a specific month and year
     */
    @Query("SELECT * FROM daily_challenges WHERE year = :year AND date >= :startDate AND date <= :endDate ORDER BY date ASC")
    suspend fun getChallengesForMonth(year: Int, startDate: LocalDate, endDate: LocalDate): List<DailyChallenge>
    
    /**
     * Get count of completed challenges
     */
    @Query("SELECT COUNT(*) FROM daily_challenges WHERE isCompleted = 1")
    fun getCompletedCount(): Flow<Int>
    
    /**
     * Get total amount saved (sum of completed challenges)
     */
    @Query("SELECT SUM(amount) FROM daily_challenges WHERE isCompleted = 1")
    fun getTotalSaved(): Flow<Int?>

    /**
     * Get total amount saved for a month
     */
    @Query("SELECT SUM(amount) FROM daily_challenges WHERE year = :year AND date >= :startDate AND date <= :endDate AND isCompleted = 1")
    fun getTotalSavedForMonth(year: Int, startDate: LocalDate, endDate: LocalDate): Flow<Int?>
    
    /**
     * Insert a new challenge
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: DailyChallenge): Long
    
    /**
     * Insert multiple challenges
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenges(challenges: List<DailyChallenge>)
    
    /**
     * Update an existing challenge
     */
    @Update
    suspend fun updateChallenge(challenge: DailyChallenge)
    
    /**
     * Delete all challenges (for reset functionality)
     */
    @Query("DELETE FROM daily_challenges")
    suspend fun deleteAllChallenges()
    
    /**
     * Delete challenges for a specific year
     */
    @Query("DELETE FROM daily_challenges WHERE year = :year")
    suspend fun deleteChallengesForYear(year: Int)
    
    /**
     * Get the latest challenge
     */
    @Query("SELECT * FROM daily_challenges ORDER BY date DESC LIMIT 1")
    suspend fun getLatestChallenge(): DailyChallenge?
}
