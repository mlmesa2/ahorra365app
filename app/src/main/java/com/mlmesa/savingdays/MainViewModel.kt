package com.mlmesa.savingdays

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mlmesa.savingdays.data.local.preferences.UserPreferences
import com.mlmesa.savingdays.data.local.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog = _showUpdateDialog.asStateFlow()

    val userPreferences: StateFlow<UserPreferences?> = userPreferencesRepository.userPreferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun onUpdateDownloaded() {
        _showUpdateDialog.value = true
    }

    fun hideUpdateDialog() {
        _showUpdateDialog.value = false
    }
}