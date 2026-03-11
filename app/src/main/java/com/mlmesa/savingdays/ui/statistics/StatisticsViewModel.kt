package com.mlmesa.savingdays.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import com.mlmesa.savingdays.data.model.CurrencyScale
import com.mlmesa.savingdays.data.repository.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

data class MonthlySaving(
    val month: Int,
    val monthName: String,
    val totalSaved: Int,
    val completedDays: Int
)

data class YearlySaving(
    val year: Int,
    val totalSaved: Int
)

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val selectedYear: Int = LocalDate.now().year,
    val monthlySavings: List<MonthlySaving> = emptyList(),
    val yearlySavings: List<YearlySaving> = emptyList(),
    val totalSavedAllTime: Int = 0,
    val availableYears: List<Int> = emptyList(),
    val totalToSave: Int = 0,
    val difference: Int = 0
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val challengeRepository: ChallengeRepository,
    preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    val currencyScale: StateFlow<CurrencyScale> = preferencesRepository.userPreferencesFlow
        .map { it.currencyScale }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CurrencyScale.GENERIC)

    init {
        observeChallengeChanges()
    }

    fun selectYear(year: Int) {
        _uiState.value = _uiState.value.copy(selectedYear = year)
        viewModelScope.launch {
            refreshData()
        }
    }

    /**
     * Observe all challenges reactively. Whenever any challenge changes
     * (completed, added, etc.), this will trigger a full data refresh.
     */
    private fun observeChallengeChanges() {
        viewModelScope.launch {
            challengeRepository.getAllChallenges().collectLatest {
                refreshData()
            }
        }
    }

    /**
     * Refresh all statistics data from the repository.
     */
    private suspend fun refreshData() {
        val currentYear = _uiState.value.selectedYear

        val years = challengeRepository.getDistinctYears().ifEmpty {
            listOf(LocalDate.now().year)
        }

        val yearlySavings = years.map { year ->
            YearlySaving(
                year = year,
                totalSaved = challengeRepository.getTotalSavedForYear(year)
            )
        }

        val totalToSave = challengeRepository.getChallengesAmountForYear(currentYear).first() ?: 0
        val totalAllTime = yearlySavings.sumOf { it.totalSaved }

        // Load monthly data for selected year
        val currentDate = LocalDate.now()
        val maxMonth = if (currentYear == currentDate.year) currentDate.monthValue else 12

        val monthlySavings = (1..maxMonth).map { month ->
            val totalSaved = challengeRepository.getTotalSavedForMonth(currentYear, month)
                .first() ?: 0

            val monthName = Month.of(month)
                .getDisplayName(TextStyle.SHORT, Locale.getDefault())
                .replaceFirstChar { it.uppercase() }

            MonthlySaving(
                month = month,
                monthName = monthName,
                totalSaved = totalSaved,
                completedDays = challengeRepository.getCompletedCountForMonth(currentYear, month)
            )
        }

        _uiState.value = StatisticsUiState(
            isLoading = false,
            selectedYear = currentYear,
            monthlySavings = monthlySavings,
            yearlySavings = yearlySavings,
            totalSavedAllTime = totalAllTime,
            availableYears = years,
            totalToSave = totalToSave,
            difference = totalToSave - totalAllTime
        )
    }
}
