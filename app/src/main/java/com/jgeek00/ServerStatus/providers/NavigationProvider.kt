package com.jgeek00.ServerStatus.providers

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationProvider: ViewModel() {
    companion object {
        private var INSTANCE: NavigationProvider? = null

        fun getInstance(): NavigationProvider {
            if (INSTANCE == null) {
                INSTANCE = NavigationProvider()
            }
            return INSTANCE!!
        }
    }


    private val _navEvent = MutableStateFlow<NavEvent?>(null)
    val navEvent: StateFlow<NavEvent?> = _navEvent

    fun navigateTo(destination: String) {
        _navEvent.value = NavEvent.Navigate(destination)
    }

    fun popBack() {
        _navEvent.value = NavEvent.PopBack
    }

    fun clearNavEvent() {
        _navEvent.value = null
    }

    sealed class NavEvent {
        data class Navigate(val destination: String) : NavEvent()
        object PopBack : NavEvent()
    }
}
