package com.jgeek00.ServerStatus.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jgeek00.ServerStatus.constants.DataStoreKeys
import com.jgeek00.ServerStatus.navigation.NavigationManager
import com.jgeek00.ServerStatus.navigation.Routes
import com.jgeek00.ServerStatus.services.DataStoreService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val dataStoreService: DataStoreService
): ViewModel() {
    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    fun nextPage() {
        _currentPage.value += 1
    }

    fun previousPage() {
        _currentPage.value -= 1
    }

    fun finish() {
        NavigationManager.getInstance().navigateReplace(Routes.ONBOARDING, Routes.ROUTE_STATUS)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                dataStoreService.setBoolean(DataStoreKeys.ONBOARDING_COMPLETED, true)
            }
        }
    }
}