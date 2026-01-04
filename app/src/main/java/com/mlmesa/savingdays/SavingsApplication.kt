package com.mlmesa.savingdays

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.data.repository.ChallengeRepository
import com.mlmesa.savingdays.util.NotificationHelper
import com.mlmesa.savingdays.util.WorkManagerScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Application class for Ahorro365.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class SavingsApplication : Application(), Configuration.Provider {
    
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    
    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository
    
    @Inject
    lateinit var challengeRepository: ChallengeRepository
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    
    override fun onCreate() {
        super.onCreate()
        
        // Create notification channel
        NotificationHelper.createNotificationChannel(this)
        
        // Initialize app data
        applicationScope.launch {
            try {
                // Initialize achievements if needed
                challengeRepository.initializeAchievements()
                
                // Schedule notifications if enabled
                val preferences = preferencesRepository.userPreferencesFlow.first()
                if (preferences.notificationsEnabled) {
                    WorkManagerScheduler.scheduleDailyNotification(
                        this@SavingsApplication,
                        preferences.notificationHour,
                        preferences.notificationMinute
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

