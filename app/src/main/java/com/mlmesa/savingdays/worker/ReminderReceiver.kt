package com.mlmesa.savingdays.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.domain.usecase.GetTodayChallengeUseCase
import com.mlmesa.savingdays.util.MotivationalMessages
import com.mlmesa.savingdays.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var dailyReminderManager: DailyNotificationReminder

    @Inject
    lateinit var getTodayChallengeUseCase: GetTodayChallengeUseCase

    @Inject
    lateinit var preferencesRepository: UserPreferencesRepository

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.IO).launch {
            val preferences = preferencesRepository.userPreferencesFlow.first()

            if (preferences.notificationsEnabled) {
                val todayChallenge = getTodayChallengeUseCase()
                if (todayChallenge != null && !todayChallenge.isCompleted) {
                    // Send notification
                    NotificationHelper.sendDailyChallengeNotification(
                        context = context,
                        amount = todayChallenge.amount,
                        currencySymbol = preferences.currencySymbol,
                        motivationalMessage = MotivationalMessages.getRandom()
                    )
                }
            }
            dailyReminderManager.reSchedule()

        }
    }
}