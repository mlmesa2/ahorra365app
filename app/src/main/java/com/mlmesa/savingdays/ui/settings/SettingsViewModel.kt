package com.mlmesa.savingdays.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mlmesa.savingdays.data.local.preferences.UserPreferences
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.data.repository.ChallengeRepository
import com.mlmesa.savingdays.domain.model.Statistics
import com.mlmesa.savingdays.domain.usecase.GenerateChallengesUseCase
import com.mlmesa.savingdays.domain.usecase.GetStatisticsUseCase
import com.mlmesa.savingdays.util.WorkManagerScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the Settings screen
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    application: Application,
    private val preferencesRepository: UserPreferencesRepository,
    private val repository: ChallengeRepository,
    private val generateChallengesUseCase: GenerateChallengesUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase
) : AndroidViewModel(application) {
    
    // User preferences
    val userPreferences: StateFlow<UserPreferences> = preferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )
    
    // Statistics
    val statistics: StateFlow<Statistics?> = getStatisticsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )
    
    // Reset confirmation dialog state
    private val _showResetDialog = MutableStateFlow(false)
    val showResetDialog: StateFlow<Boolean> = _showResetDialog.asStateFlow()
    
    // Currency options
    val currencyOptions = listOf("$", "€", "MXN", "£", "¥", "Custom")
    
    /**
     * Toggle notifications enabled/disabled
     */
    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
            
            if (enabled) {
                val prefs = userPreferences.value
                WorkManagerScheduler.scheduleDailyNotification(
                    getApplication(),
                    prefs.notificationHour,
                    prefs.notificationMinute
                )
            } else {
                WorkManagerScheduler.cancelDailyNotification(getApplication())
            }
        }
    }
    
    /**
     * Update notification time
     */
    fun updateNotificationTime(hour: Int, minute: Int) {
        viewModelScope.launch {
            preferencesRepository.setNotificationTime(hour, minute)
            
            // Reschedule if notifications are enabled
            if (userPreferences.value.notificationsEnabled) {
                WorkManagerScheduler.scheduleDailyNotification(
                    getApplication(),
                    hour,
                    minute
                )
            }
        }
    }
    
    /**
     * Update currency symbol
     */
    fun updateCurrency(symbol: String) {
        viewModelScope.launch {
            preferencesRepository.setCurrencySymbol(symbol)
        }
    }
    
    /**
     * Toggle auto-reset on January 1st
     */
    fun toggleAutoReset(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setAutoResetEnabled(enabled)
        }
    }
    
    /**
     * Show reset confirmation dialog
     */
    fun showResetConfirmation() {
        _showResetDialog.value = true
    }
    
    /**
     * Hide reset confirmation dialog
     */
    fun hideResetConfirmation() {
        _showResetDialog.value = false
    }
    
    /**
     * Reset the challenge
     */
    fun resetChallenge() {
        viewModelScope.launch {
            try {
                // Delete all existing challenges and achievements
                repository.deleteAllChallenges()
                repository.deleteAllAchievements()
                
                // Initialize new achievements
                repository.initializeAchievements()
                
                // Generate new challenges for the current year (from Jan 1)
                val currentYear = LocalDate.now().year
                generateChallengesUseCase(currentYear)
                
                // Reset preferences
                preferencesRepository.resetPreferences()
                
                // Set start date to January 1st of current year
                preferencesRepository.setChallengeStartDate(LocalDate.of(currentYear, 1, 1))
                
                // Hide dialog
                _showResetDialog.value = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
