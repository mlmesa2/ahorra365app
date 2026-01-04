package com.mlmesa.savingdays.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mlmesa.savingdays.data.local.converter.Converters
import com.mlmesa.savingdays.data.local.dao.AchievementDao
import com.mlmesa.savingdays.data.local.dao.DailyChallengeDao
import com.mlmesa.savingdays.data.local.entity.Achievement
import com.mlmesa.savingdays.data.local.entity.DailyChallenge

/**
 * Room database for the Ahorro365 app.
 * Contains tables for daily challenges and achievements.
 */
@Database(
    entities = [DailyChallenge::class, Achievement::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SavingsDatabase : RoomDatabase() {
    
    abstract fun dailyChallengeDao(): DailyChallengeDao
    abstract fun achievementDao(): AchievementDao
    
    companion object {
        const val DATABASE_NAME = "savings_database"
    }
}
