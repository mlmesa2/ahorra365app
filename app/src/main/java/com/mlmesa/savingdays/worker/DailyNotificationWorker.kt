package com.mlmesa.savingdays.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.domain.usecase.GetTodayChallengeUseCase
import com.mlmesa.savingdays.util.MotivationalMessages
import com.mlmesa.savingdays.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * WorkManager worker for sending daily notifications
 */
@HiltWorker
class DailyNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val getTodayChallengeUseCase: GetTodayChallengeUseCase,
    private val preferencesRepository: UserPreferencesRepository
) : CoroutineWorker(context, workerParams) {
    
    override suspend fun doWork(): Result {
        return try {
            // Check if notifications are enabled
            val preferences = preferencesRepository.userPreferencesFlow.first()
            
            if (!preferences.notificationsEnabled) {
                return Result.success()
            }
            
            // Get today's challenge
            val todayChallenge = getTodayChallengeUseCase()
            
            if (todayChallenge != null && !todayChallenge.isCompleted) {
                // Send notification
                NotificationHelper.sendDailyChallengeNotification(
                    context = applicationContext,
                    amount = todayChallenge.amount,
                    currencySymbol = preferences.currencySymbol,
                    motivationalMessage = MotivationalMessages.getRandom()
                )
            }
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
