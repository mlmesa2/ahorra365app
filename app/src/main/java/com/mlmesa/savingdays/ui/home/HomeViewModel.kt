package com.mlmesa.savingdays.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mlmesa.savingdays.data.local.entity.Achievement
import com.mlmesa.savingdays.data.local.entity.DailyChallenge
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.domain.model.Statistics
import com.mlmesa.savingdays.domain.usecase.CheckAchievementsUseCase
import com.mlmesa.savingdays.domain.usecase.CompleteChallengeUseCase
import com.mlmesa.savingdays.domain.usecase.GetStatisticsUseCase
import com.mlmesa.savingdays.domain.usecase.GetTodayChallengeUseCase
import com.mlmesa.savingdays.util.MotivationalMessages
import com.mlmesa.savingdays.worker.DailyNotificationReminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Home screen
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val getTodayChallengeUseCase: GetTodayChallengeUseCase,
    private val completeChallengeUseCase: CompleteChallengeUseCase,
    private val getStatisticsUseCase: GetStatisticsUseCase,
    private val checkAchievementsUseCase: CheckAchievementsUseCase,
    private val preferencesRepository: UserPreferencesRepository,
    private val dailyNotificationReminder: DailyNotificationReminder
) : AndroidViewModel(application)  {


    // State for today's challenge
    private val _todayChallenge = MutableStateFlow<DailyChallenge?>(null)
    val todayChallenge: StateFlow<DailyChallenge?> = _todayChallenge.asStateFlow()
    
    // State for statistics
    private val _statistics = MutableStateFlow<Statistics?>(null)
    val statistics: StateFlow<Statistics?> = _statistics.asStateFlow()
    
    // State for motivational message
    private val _motivationalMessage = MutableStateFlow(MotivationalMessages.getRandom())
    val motivationalMessage: StateFlow<String> = _motivationalMessage.asStateFlow()
    
    // State for currency symbol
    val currencySymbol: StateFlow<String> = preferencesRepository.userPreferencesFlow
        .map { it.currencySymbol }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "$"
        )
    // User preferences
    val notificationsEnabled: StateFlow<Boolean> = preferencesRepository.userPreferencesFlow
        .map { it.notificationsEnabled }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    // State for newly unlocked achievements
    private val _newlyUnlockedAchievements = MutableStateFlow<List<Achievement>>(emptyList())
    val newlyUnlockedAchievements: StateFlow<List<Achievement>> = _newlyUnlockedAchievements.asStateFlow()
    
    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadTodayChallenge()
        observeStatistics()
    }
    
    /**
     * Load today's challenge and observe changes
     */
    private fun loadTodayChallenge() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // First ensure challenge exists (legacy check)
                getTodayChallengeUseCase()
                
                // Now observe it
                getTodayChallengeUseCase.getTodayChallengeFlow()
                    .collect { challenge ->
                        _todayChallenge.value = challenge
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Observe statistics
     */
    private fun observeStatistics() {
        viewModelScope.launch {
            getStatisticsUseCase()
                .catch { e -> 
                    e.printStackTrace()
                }
                .collect { stats ->
                    _statistics.value = stats
                }
        }
    }
    
    /**
     * Mark today's challenge as completed
     */
    fun completeChallenge() {
        viewModelScope.launch {
            val challenge = _todayChallenge.value ?: return@launch
            
            if (!challenge.isCompleted) {
                try {
                    // Complete the challenge
                    completeChallengeUseCase(challenge)
                    
                    // Update the local state
                    _todayChallenge.value = challenge.copy(isCompleted = true)
                    
                    // Check for newly unlocked achievements
                    val newAchievements = checkAchievementsUseCase()
                    if (newAchievements.isNotEmpty()) {
                        _newlyUnlockedAchievements.value = newAchievements
                    }
                    
                    // Refresh motivational message
                    _motivationalMessage.value = MotivationalMessages.getRandom()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    
    /**
     * Refresh today's challenge
     */
    fun refresh() {
        loadTodayChallenge()
        _motivationalMessage.value = MotivationalMessages.getRandom()
    }
    
    /**
     * Clear newly unlocked achievements notification
     */
    fun clearNewAchievements() {
        _newlyUnlockedAchievements.value = emptyList()
    }

    /**
     * Toggle notifications on/off
     */
    fun toggleNotificationOnOff(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setNotificationsEnabled(enabled)
            dailyNotificationReminder.cancel()
        }
    }
}
