package com.mlmesa.savingdays

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _showUpdateDialog = MutableStateFlow(false)
    val showUpdateDialog = _showUpdateDialog.asStateFlow()

    fun onUpdateDownloaded() {
        _showUpdateDialog.value = true
    }

    fun hideUpdateDialog() {
        _showUpdateDialog.value = false
    }
}