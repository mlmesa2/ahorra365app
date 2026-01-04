package com.mlmesa.savingdays.di

import android.content.Context
import androidx.room.Room
import com.mlmesa.savingdays.data.local.SavingsDatabase
import com.mlmesa.savingdays.data.local.dao.AchievementDao
import com.mlmesa.savingdays.data.local.dao.DailyChallengeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideSavingsDatabase(
        @ApplicationContext context: Context
    ): SavingsDatabase {
        return Room.databaseBuilder(
            context,
            SavingsDatabase::class.java,
            SavingsDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideDailyChallengeDao(database: SavingsDatabase): DailyChallengeDao {
        return database.dailyChallengeDao()
    }
    
    @Provides
    @Singleton
    fun provideAchievementDao(database: SavingsDatabase): AchievementDao {
        return database.achievementDao()
    }
}
