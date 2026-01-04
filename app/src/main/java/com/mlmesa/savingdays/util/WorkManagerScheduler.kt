package com.mlmesa.savingdays.util

import android.content.Context
import androidx.work.*
import com.mlmesa.savingdays.worker.DailyNotificationWorker
import java.util.concurrent.TimeUnit

/**
 * Utility for scheduling WorkManager tasks
 */
object WorkManagerScheduler {
    
    /**
     * Schedule daily notification
     */
    fun scheduleDailyNotification(
        context: Context,
        hour: Int,
        minute: Int
    ) {
        // Cancel existing work
        WorkManager.getInstance(context).cancelUniqueWork(Constants.WORK_NAME_DAILY_NOTIFICATION)
        
        // Calculate initial delay
        val currentTime = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.HOUR_OF_DAY, hour)
            set(java.util.Calendar.MINUTE, minute)
            set(java.util.Calendar.SECOND, 0)
            
            // If the time has already passed today, schedule for tomorrow
            if (timeInMillis <= currentTime) {
                add(java.util.Calendar.DAY_OF_MONTH, 1)
            }
        }
        
        val initialDelay = calendar.timeInMillis - currentTime
        
        // Create work request
        val workRequest = PeriodicWorkRequestBuilder<DailyNotificationWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.DAYS
        )
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()
        
        // Enqueue work
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            Constants.WORK_NAME_DAILY_NOTIFICATION,
            ExistingPeriodicWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    /**
     * Cancel daily notification
     */
    fun cancelDailyNotification(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(Constants.WORK_NAME_DAILY_NOTIFICATION)
    }
}
