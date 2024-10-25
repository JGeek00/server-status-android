package com.jgeek00.ServerStatus.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jgeek00.ServerStatus.views.SettingsView
import com.jgeek00.ServerStatus.views.StatusView

@Composable
fun NavigationManager() {
    val navigationController = rememberNavController()

    NavHost(
        navController = navigationController,
        startDestination = "/status"
    ) {
        composable(route = "/status") {
            StatusView(navigationController)
        }
        composable(route = "/settings") {
            SettingsView(navigationController)
        }
    }
}