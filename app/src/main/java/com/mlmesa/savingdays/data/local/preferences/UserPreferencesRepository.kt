package com.mlmesa.savingdays.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.time.LocalDate
import com.mlmesa.savingdays.data.model.CurrencyScale

/**
 * Extension property to create DataStore instance
 */
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

/**
 * Repository for managing user preferences using DataStore
 */
class UserPreferencesRepository(private val context: Context) {
    
    private val dataStore = context.dataStore
    
    companion object {
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val NOTIFICATION_HOUR = intPreferencesKey("notification_hour")
        private val NOTIFICATION_MINUTE = intPreferencesKey("notification_minute")
        private val CURRENCY_SYMBOL = stringPreferencesKey("currency_symbol")
        private val AUTO_RESET_ENABLED = booleanPreferencesKey("auto_reset_enabled")
        private val CHALLENGE_START_DATE = stringPreferencesKey("challenge_start_date")
        private val CURRENT_STREAK = intPreferencesKey("current_streak")
        private val LONGEST_STREAK = intPreferencesKey("longest_streak")
        private val LAST_COMPLETED_DATE = stringPreferencesKey("last_completed_date")
        private val CURRENCY_SCALE = stringPreferencesKey("currency_scale")
        private val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val COMPLETED_DAYS_COUNT = intPreferencesKey("completed_days_count")
        private val REVIEW_SHOWN = booleanPreferencesKey("review_shown")
        private val REVIEW_ATTEMPTS = intPreferencesKey("review_attempts")
        private val REVIEW_LAST_ATTEMPT_DATE = stringPreferencesKey("review_last_attempt_date")
    }
    
    /**
     * Flow of user preferences
     */
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                notificationsEnabled = preferences[NOTIFICATIONS_ENABLED] ?: true,
                notificationHour = preferences[NOTIFICATION_HOUR] ?: 9,
                notificationMinute = preferences[NOTIFICATION_MINUTE] ?: 0,
                currencySymbol = preferences[CURRENCY_SYMBOL] ?: "$",
                autoResetEnabled = preferences[AUTO_RESET_ENABLED] ?: true,
                challengeStartDate = preferences[CHALLENGE_START_DATE]?.let { LocalDate.parse(it) },
                currentStreak = preferences[CURRENT_STREAK] ?: 0,
                longestStreak = preferences[LONGEST_STREAK] ?: 0,
                lastCompletedDate = preferences[LAST_COMPLETED_DATE]?.let { LocalDate.parse(it) },
                currencyScale = preferences[CURRENCY_SCALE]?.let {
                    try { CurrencyScale.valueOf(it) } catch (e: Exception) { CurrencyScale.GENERIC }
                } ?: CurrencyScale.GENERIC,
                onboardingCompleted = preferences[ONBOARDING_COMPLETED] ?: false,
                completedDaysCount = preferences[COMPLETED_DAYS_COUNT] ?: 0,
                reviewShown = preferences[REVIEW_SHOWN] ?: false,
                reviewAttempts = preferences[REVIEW_ATTEMPTS] ?: 0,
                reviewLastAttemptDate = preferences[REVIEW_LAST_ATTEMPT_DATE]?.let { LocalDate.parse(it) }
            )
        }
    
    /**
     * Update notifications enabled setting
     */
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    /**
     * Update notification time
     */
    suspend fun setNotificationTime(hour: Int, minute: Int) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_HOUR] = hour
            preferences[NOTIFICATION_MINUTE] = minute
        }
    }
    
    /**
     * Update currency symbol
     */
    suspend fun setCurrencySymbol(symbol: String) {
        dataStore.edit { preferences ->
            preferences[CURRENCY_SYMBOL] = symbol
        }
    }
    
    /**
     * Update auto-reset setting
     */
    suspend fun setAutoResetEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_RESET_ENABLED] = enabled
        }
    }
    
    /**
     * Update challenge start date
     */
    suspend fun setChallengeStartDate(date: LocalDate) {
        dataStore.edit { preferences ->
            preferences[CHALLENGE_START_DATE] = date.toString()
        }
    }
    
    /**
     * Update current streak
     */
    suspend fun setCurrentStreak(streak: Int) {
        dataStore.edit { preferences ->
            preferences[CURRENT_STREAK] = streak
            // Update longest streak if current is higher
            val longestStreak = preferences[LONGEST_STREAK] ?: 0
            if (streak > longestStreak) {
                preferences[LONGEST_STREAK] = streak
            }
        }
    }
    
    /**
     * Update last completed date
     */
    suspend fun setLastCompletedDate(date: LocalDate) {
        dataStore.edit { preferences ->
            preferences[LAST_COMPLETED_DATE] = date.toString()
        }
    }
    
    /**
     * Reset all preferences (for challenge reset)
     */
    suspend fun resetPreferences() {
        dataStore.edit { preferences ->
            preferences[CURRENT_STREAK] = 0
            preferences[CHALLENGE_START_DATE] = LocalDate.now().toString()
            preferences.remove(LAST_COMPLETED_DATE)
        }
    }

    /**
     * Update currency scale
     */
    suspend fun setCurrencyScale(scale: CurrencyScale) {
        dataStore.edit { preferences ->
            preferences[CURRENCY_SCALE] = scale.name
            // Also update the symbol for backwards compatibility
            preferences[CURRENCY_SYMBOL] = scale.symbol
        }
    }

    /**
     * Mark onboarding as completed
     */
    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED] = completed
        }
    }

    /**
     * Increment completed days count
     */
    suspend fun incrementCompletedDaysCount() {
        dataStore.edit { preferences ->
            val currentCount = preferences[COMPLETED_DAYS_COUNT] ?: 0
            preferences[COMPLETED_DAYS_COUNT] = currentCount + 1
        }
    }

    /**
     * Get completed days count
     */
    suspend fun getCompletedDaysCount(): Flow<Int> {
        return userPreferencesFlow.map { it.completedDaysCount }
    }

    /**
     * Mark review as shown
     */
    suspend fun setReviewShown(shown: Boolean) {
        dataStore.edit { preferences ->
            preferences[REVIEW_SHOWN] = shown
        }
    }

    /**
     * Increment review attempts counter
     */
    suspend fun incrementReviewAttempts() {
        dataStore.edit { preferences ->
            val currentAttempts = preferences[REVIEW_ATTEMPTS] ?: 0
            preferences[REVIEW_ATTEMPTS] = currentAttempts + 1
            preferences[REVIEW_LAST_ATTEMPT_DATE] = LocalDate.now().toString()
        }
    }
}

data class UserPreferences(
    val notificationsEnabled: Boolean = true,
    val notificationHour: Int = 9,
    val notificationMinute: Int = 0,
    val currencySymbol: String = "$",
    val autoResetEnabled: Boolean = true,
    val challengeStartDate: LocalDate? = null,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastCompletedDate: LocalDate? = null,
    val currencyScale: CurrencyScale = CurrencyScale.GENERIC,
    val onboardingCompleted: Boolean = false,
    val completedDaysCount: Int = 0,
    val reviewShown: Boolean = false,
    val reviewAttempts: Int = 0,
    val reviewLastAttemptDate: LocalDate? = null
)
