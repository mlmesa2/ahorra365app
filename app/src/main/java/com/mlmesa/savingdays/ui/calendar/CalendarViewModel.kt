package com.mlmesa.savingdays.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mlmesa.savingdays.data.local.entity.DailyChallenge
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.data.model.CurrencyScale
import com.mlmesa.savingdays.data.repository.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * ViewModel for the Calendar screen
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: ChallengeRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {
    
    // Current year-month being viewed
    private val _currentYearMonth = MutableStateFlow(YearMonth.now())
    val currentYearMonth: StateFlow<YearMonth> = _currentYearMonth.asStateFlow()
    
    // Challenges for the current month
    private val _monthChallenges = MutableStateFlow<List<DailyChallenge>>(emptyList())
    val monthChallenges: StateFlow<List<DailyChallenge>> = _monthChallenges.asStateFlow()
    
    // All challenges (for quick lookup)
    private val _allChallenges = MutableStateFlow<List<DailyChallenge>>(emptyList())
    val allChallenges: StateFlow<List<DailyChallenge>> = _allChallenges.asStateFlow()
    
    // Selected challenge details
    private val _selectedChallenge = MutableStateFlow<DailyChallenge?>(null)
    val selectedChallenge: StateFlow<DailyChallenge?> = _selectedChallenge.asStateFlow()

    // Currency scale for amount display
    val currencyScale: StateFlow<CurrencyScale> = preferencesRepository.userPreferencesFlow
        .map { it.currencyScale }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CurrencyScale.GENERIC
        )
    
    init {
        observeAllChallenges()
        loadMonthChallenges()

    }
    
    /**
     * Observe all challenges
     */
    private fun observeAllChallenges() {
        viewModelScope.launch {
            repository.getAllChallenges()
                .catch { e -> e.printStackTrace() }
                .collect { challenges ->
                    _allChallenges.value = challenges
                }
        }
    }
    
    /**
     * Load challenges for the current month
     */
    private fun loadMonthChallenges() {
        viewModelScope.launch {
            try {
                val yearMonth = _currentYearMonth.value
                val challenges = repository.getChallengesForMonth(
                    yearMonth.year,
                    yearMonth.monthValue
                )
                _monthChallenges.value = challenges
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Navigate to the previous month
     */
    fun previousMonth() {
        _currentYearMonth.value = _currentYearMonth.value.minusMonths(1)
        loadMonthChallenges()
    }
    
    /**
     * Navigate to the next month
     */
    fun nextMonth() {
        _currentYearMonth.value = _currentYearMonth.value.plusMonths(1)
        loadMonthChallenges()
    }
    
    /**
     * Go to today's month
     */
    fun goToToday() {
        _currentYearMonth.value = YearMonth.now()
        loadMonthChallenges()
        selectChallenge(LocalDate.now())
    }
    
    /**
     * Select a challenge to view details
     */
    fun selectChallenge(date: LocalDate) {
        val challenge = _allChallenges.value.find { it.date == date }
        _selectedChallenge.value = challenge
    }
    
    /**
     * Clear selected challenge
     */
    fun clearSelection() {
        _selectedChallenge.value = null
    }
    
    /**
     * Get challenge for a specific date
     */
    fun getChallengeForDate(date: LocalDate): DailyChallenge? {
        return _allChallenges.value.find { it.date == date }
    }
    
    /**
     * Complete a challenge (for past days)
     */
    fun completeChallenge(challenge: DailyChallenge) {
        viewModelScope.launch {
            try {
                repository.completeChallenge(challenge)
                
                // Update the selected challenge state to reflect completion immediately
                // This ensures the UI updates without waiting for the full list reload
                val updatedChallenge = challenge.copy(
                    isCompleted = true,
                    completedDate = LocalDate.now()
                )
                _selectedChallenge.value = updatedChallenge
                
                // Refresh data
                loadMonthChallenges()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
