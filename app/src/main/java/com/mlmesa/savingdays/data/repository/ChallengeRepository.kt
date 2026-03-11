package com.mlmesa.savingdays.data.repository

import com.mlmesa.savingdays.data.local.dao.AchievementDao
import com.mlmesa.savingdays.data.local.dao.DailyChallengeDao
import com.mlmesa.savingdays.data.local.entity.Achievement
import com.mlmesa.savingdays.data.local.entity.AchievementType
import com.mlmesa.savingdays.data.local.entity.DailyChallenge
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing daily challenges and related operations.
 * Acts as a single source of truth for challenge data.
 */
@Singleton
class ChallengeRepository @Inject constructor(
    private val dailyChallengeDao: DailyChallengeDao,
    private val achievementDao: AchievementDao
) {
    
    /**
     * Get all challenges as a Flow
     */
    fun getAllChallenges(): Flow<List<DailyChallenge>> {
        return dailyChallengeDao.getAllChallenges()
    }
    
    /**
     * Get challenge for a specific date
     */
    suspend fun getChallengeByDate(date: LocalDate): DailyChallenge? {
        return dailyChallengeDao.getChallengeByDate(date)
    }
    
    /**
     * Get today's challenge
     */
    suspend fun getTodayChallenge(): DailyChallenge? {
        return dailyChallengeDao.getChallengeByDate(LocalDate.now())
    }

    /**
     * Get today's challenge as a Flow
     */
    fun getTodayChallengeFlow(): Flow<DailyChallenge?> {
        return dailyChallengeDao.getChallengeByDateFlow(LocalDate.now())
    }
    
    /**
     * Get all completed challenges
     */
    fun getCompletedChallenges(): Flow<List<DailyChallenge>> {
        return dailyChallengeDao.getCompletedChallenges()
    }
    
    /**
     * Get challenges for a specific year
     */
    fun getChallengesForYear(year: Int): Flow<List<DailyChallenge>> {
        return dailyChallengeDao.getChallengesForYear(year)
    }
    
    /**
     * Get challenges for a specific month
     */
    suspend fun getChallengesForMonth(year: Int, month: Int): List<DailyChallenge> {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.plusMonths(1).minusDays(1)
        return dailyChallengeDao.getChallengesForMonth(year, startDate, endDate)
    }
    
    /**
     * Get count of completed challenges
     */
    fun getCompletedCount(): Flow<Int> {
        return dailyChallengeDao.getCompletedCount()
    }
    
    /**
     * Get total amount saved
     */
    fun getTotalSaved(): Flow<Int?> {
        return dailyChallengeDao.getTotalSaved()
    }

    /**
     * Get total amount saved for a month
     */
     fun getTotalSavedForMonth(year: Int, month: Int): Flow<Int?> {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.plusMonths(1).minusDays(1)
        return dailyChallengeDao.getTotalSavedForMonth(year,  startDate, endDate)
     }

    /**
     * Get count of completed challenges for a month
     */
    suspend fun getCompletedCountForMonth(year: Int, month: Int): Int {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.plusMonths(1).minusDays(1)
        return dailyChallengeDao.getCompletedCountForMonth(year, startDate, endDate)
    }

    /**
     * Get total saved for a specific year
     */
    suspend fun getTotalSavedForYear(year: Int): Int {
        return dailyChallengeDao.getTotalSavedForYear(year)
    }

    /**
     * Get all distinct years with challenges
     */
    suspend fun getDistinctYears(): List<Int> {
        return dailyChallengeDao.getDistinctYears()
    }

    /**
     * Get all challenges amount for a specific year
     */
    fun getChallengesAmountForYear(year: Int): Flow<Int?> {
        return dailyChallengeDao.getChallengesAmountForYear(year)
    }

    
    /**
     * Insert a new challenge
     */
    suspend fun insertChallenge(challenge: DailyChallenge): Long {
        return dailyChallengeDao.insertChallenge(challenge)
    }
    
    /**
     * Insert multiple challenges
     */
    suspend fun insertChallenges(challenges: List<DailyChallenge>) {
        dailyChallengeDao.insertChallenges(challenges)
    }
    
    /**
     * Mark a challenge as completed
     */
    suspend fun completeChallenge(challenge: DailyChallenge) {
        val updatedChallenge = challenge.copy(
            isCompleted = true,
            completedDate = LocalDate.now()
        )
        dailyChallengeDao.updateChallenge(updatedChallenge)
    }
    
    /**
     * Update a challenge
     */
    suspend fun updateChallenge(challenge: DailyChallenge) {
        dailyChallengeDao.updateChallenge(challenge)
    }
    
    /**
     * Delete all challenges (for reset)
     */
    suspend fun deleteAllChallenges() {
        dailyChallengeDao.deleteAllChallenges()
    }
    
    /**
     * Delete challenges for a specific year
     */
    suspend fun deleteChallengesForYear(year: Int) {
        dailyChallengeDao.deleteChallengesForYear(year)
    }
    
    /**
     * Get the latest challenge
     */
    suspend fun getLatestChallenge(): DailyChallenge? {
        return dailyChallengeDao.getLatestChallenge()
    }
    
    // Achievement methods
    
    /**
     * Get all achievements
     */
    fun getAllAchievements(): Flow<List<Achievement>> {
        return achievementDao.getAllAchievements()
    }
    
    /**
     * Get achievement by type
     */
    suspend fun getAchievementByType(type: AchievementType): Achievement? {
        return achievementDao.getAchievementByType(type)
    }
    
    /**
     * Get unlocked achievements
     */
    fun getUnlockedAchievements(): Flow<List<Achievement>> {
        return achievementDao.getUnlockedAchievements()
    }
    
    /**
     * Get count of unlocked achievements
     */
    fun getUnlockedAchievementsCount(): Flow<Int> {
        return achievementDao.getUnlockedCount()
    }
    
    /**
     * Insert achievement
     */
    suspend fun insertAchievement(achievement: Achievement): Long {
        return achievementDao.insertAchievement(achievement)
    }
    
    /**
     * Insert multiple achievements (for initialization)
     */
    suspend fun insertAchievements(achievements: List<Achievement>) {
        achievementDao.insertAchievements(achievements)
    }
    
    /**
     * Update achievement
     */
    suspend fun updateAchievement(achievement: Achievement) {
        achievementDao.updateAchievement(achievement)
    }
    
    /**
     * Delete all achievements (for reset)
     */
    suspend fun deleteAllAchievements() {
        achievementDao.deleteAllAchievements()
    }
    
    /**
     * Initialize achievements if they don't exist
     */
    suspend fun initializeAchievements() {
        // Iterate through all achievement types
        for (type in AchievementType.values()) {
            // Check if this achievement type already exists in the database
            val existing = achievementDao.getAchievementByType(type)
            
            // Only insert if it doesn't exist
            if (existing == null) {
                achievementDao.insertAchievement(
                    Achievement(
                        type = type,
                        isUnlocked = false,
                        unlockedDate = null
                    )
                )
            }
        }
    }
}
