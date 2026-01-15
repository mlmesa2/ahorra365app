package com.mlmesa.savingdays.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class DailyNotificationReminder (
    private val context: Context,
    private val preferencesManager: UserPreferencesRepository,
    private val alarmManager: AlarmManager,
) {
    private fun getPendingIntent(
        create: Boolean
    ): PendingIntent? {
        val intent = Intent(context, ReminderReceiver::class.java)
        val flag = if (create) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        }

        return PendingIntent.getBroadcast(context, 0, intent, flag)
    }

    fun reSchedule() {
        getPendingIntent(false)?.let {
            alarmManager.cancel(it)
        }
        CoroutineScope(Dispatchers.IO).launch {
            scheduleReminder()
        }
    }

    private suspend fun scheduleReminder() {
        val pendingIntent = getPendingIntent(true) ?: return
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            getTriggerAtMillis(),
            pendingIntent
        )
    }

    private suspend fun getTriggerAtMillis(): Long {
        val preferences = preferencesManager.userPreferencesFlow.first()
        val currentTimeMillis = System.currentTimeMillis()
        val targetTime = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, preferences.notificationHour)
            set(Calendar.MINUTE, preferences.notificationMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (targetTime.timeInMillis <= currentTimeMillis) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        return targetTime.timeInMillis
    }

    fun cancel() {
        getPendingIntent(false)?.let {
            alarmManager.cancel(it)
        }
    }
}