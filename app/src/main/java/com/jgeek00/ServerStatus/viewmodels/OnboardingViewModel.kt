package com.jgeek00.ServerStatus.viewmodels

import androidx.lifecycle.ViewModel
import com.jgeek00.ServerStatus.navigation.NavigationManager
import com.jgeek00.ServerStatus.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel: ViewModel() {
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
    }
}