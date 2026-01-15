package com.mlmesa.savingdays.di

import android.app.AlarmManager
import android.content.Context
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.worker.DailyNotificationReminder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing DataStore dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    
    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        @ApplicationContext context: Context
    ): UserPreferencesRepository {
        return UserPreferencesRepository(context)
    }

    @Provides
    fun provideReminderManager(
        @ApplicationContext context: Context,
        preferencesManager: UserPreferencesRepository
    ): DailyNotificationReminder = DailyNotificationReminder(
        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager,
        context = context,
        preferencesManager = preferencesManager
    )
}
