package com.jgeek00.ServerStatus.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationManager {
    companion object {
        private var INSTANCE: NavigationManager? = null

        fun getInstance(): NavigationManager {
            if (INSTANCE == null) {
                INSTANCE = NavigationManager()
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

    fun navigateReplace(origin: String, destination: String) {
        _navEvent.value = NavEvent.Replace(origin, destination)
    }

    sealed class NavEvent {
        data class Navigate(val destination: String) : NavEvent()
        object PopBack : NavEvent()
        data class Replace(val origin: String, val destination: String) : NavEvent()
    }
}
