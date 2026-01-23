package com.mlmesa.savingdays.ui.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.data.model.CurrencyScale
import com.mlmesa.savingdays.worker.DailyNotificationReminder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

/**
 * ViewModel for the Onboarding screen
 */
@HiltViewModel
class OnboardingViewModel @Inject constructor(
    application: Application,
    private val preferencesRepository: UserPreferencesRepository,
    private val dailyNotificationReminder: DailyNotificationReminder
) : AndroidViewModel(application) {

    // Current page index
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    // Selected currency scale (auto-detected from locale initially)
    private val _selectedScale = MutableStateFlow(CurrencyScale.fromLocale(Locale.getDefault()))
    val selectedScale: StateFlow<CurrencyScale> = _selectedScale.asStateFlow()

    // Notifications enabled state
    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    // Onboarding completed state
    private val _onboardingCompleted = MutableStateFlow(false)
    val onboardingCompleted: StateFlow<Boolean> = _onboardingCompleted.asStateFlow()

    /**
     * Move to the next page
     */
    fun nextPage() {
        _currentPage.value = (_currentPage.value + 1).coerceAtMost(3)
    }

    /**
     * Move to the previous page
     */
    fun previousPage() {
        _currentPage.value = (_currentPage.value - 1).coerceAtLeast(0)
    }

    /**
     * Set the current page
     */
    fun setPage(page: Int) {
        _currentPage.value = page.coerceIn(0, 3)
    }

    /**
     * Update selected currency scale
     */
    fun selectCurrencyScale(scale: CurrencyScale) {
        _selectedScale.value = scale
    }

    /**
     * Toggle notifications enabled
     */
    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    /**
     * Complete the onboarding and save preferences
     */
    fun completeOnboarding() {
        viewModelScope.launch {
            // Save currency scale
            preferencesRepository.setCurrencyScale(_selectedScale.value)

            // Save notifications preference
            preferencesRepository.setNotificationsEnabled(_notificationsEnabled.value)

            // Schedule notifications if enabled
            if (_notificationsEnabled.value) {
                dailyNotificationReminder.reSchedule()
            }

            // Mark onboarding as completed
            preferencesRepository.setOnboardingCompleted(true)

            _onboardingCompleted.value = true
        }
    }
}
