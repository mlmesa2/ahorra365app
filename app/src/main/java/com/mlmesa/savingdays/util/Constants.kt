package com.mlmesa.savingdays.util

/**
 * App-wide constants
 */
object Constants {
    
    // Database
    const val DATABASE_NAME = "savings_database"
    
    // Notifications
    const val NOTIFICATION_CHANNEL_ID = "daily_challenge_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Daily Challenge"
    const val NOTIFICATION_ID = 1001
    const val WORK_NAME_DAILY_NOTIFICATION = "daily_notification_work"
    
    // DataStore
    const val DATASTORE_NAME = "user_preferences"
    
    // Achievement thresholds
    const val ACHIEVEMENT_STREAK_7 = 7
    const val ACHIEVEMENT_DAYS_30 = 30
    const val ACHIEVEMENT_DAYS_100 = 100
    const val ACHIEVEMENT_HALF_COMPLETE = 183 // 50% of 365
    const val ACHIEVEMENT_COMPLETE = 365
    
    // Default values
    const val DEFAULT_NOTIFICATION_HOUR = 9
    const val DEFAULT_NOTIFICATION_MINUTE = 0
    const val DEFAULT_CURRENCY = "$"
    
    // Total days in challenge
    const val TOTAL_CHALLENGE_DAYS = 365
}
