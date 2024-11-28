package com.jgeek00.ServerStatus.navigation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jgeek00.ServerStatus.views.Onboarding.OnboardingView
import com.jgeek00.ServerStatus.views.ServerFormView
import com.jgeek00.ServerStatus.views.Settings.SettingsView
import com.jgeek00.ServerStatus.views.Status.Details.CpuDetails
import com.jgeek00.ServerStatus.views.Status.Details.MemoryDetails
import com.jgeek00.ServerStatus.views.Status.Details.NetworkDetails
import com.jgeek00.ServerStatus.views.Status.Details.StorageDetails
import com.jgeek00.ServerStatus.views.Status.StatusView

@Composable
fun AppNavigation() {
    val navigationController = rememberNavController()
    val navEvent by NavigationManager.getInstance().navEvent.collectAsState()

    val slideTime = 500

    // https://cubic-bezier.com/#.55,0,0,1
    val easing = CubicBezierEasing(0.2f, 0.7f, 0.1f, 1f)
    // val easing = CubicBezierEasing(0.55f, 0.0f, 0.0f, 1f)

    val enterTransition = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(slideTime, easing = easing)) + fadeIn(animationSpec = tween(slideTime, easing = easing))
    val exitTransition = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(slideTime, easing = easing)) + fadeOut(animationSpec = tween(slideTime, easing = easing))

    val popExitTransition = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(slideTime, easing = easing)) + fadeOut(animationSpec = tween(slideTime, easing = easing))
    val popEnterTransition = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(slideTime, easing = easing)) + fadeIn(animationSpec = tween(slideTime, easing = easing))

    LaunchedEffect(navEvent) {
        when (val event = navEvent) {
            is NavigationManager.NavEvent.Navigate -> {
                navigationController.navigate(event.destination)
                NavigationManager.getInstance().clearNavEvent()
            }
            is NavigationManager.NavEvent.PopBack -> {
                navigationController.popBackStack()
                NavigationManager.getInstance().clearNavEvent()
            }
            is NavigationManager.NavEvent.Replace -> {
                navigationController.popBackStack(event.origin, true)
                navigationController.navigate(event.destination)
                NavigationManager.getInstance().clearNavEvent()
            }
            else -> Unit
        }
    }

    NavHost(
        navController = navigationController,
        startDestination = if (NavigationManager.getInstance().onboardingCompleted.value) Routes.ROUTE_STATUS else Routes.ONBOARDING
    ) {
        composable(
            route = Routes.ROUTE_STATUS,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            StatusView()
        }
        composable(
            route = Routes.ROUTE_SETTINGS,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            SettingsView()
        }
        composable(
            route = Routes.ROUTE_SERVER_FORM,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition },
            arguments = listOf(
                navArgument(name = Routes.ARG_SERVER_ID) { type = NavType.StringType; nullable = true },
            )
        ) {
            ServerFormView(editServerId = it.arguments?.getString(Routes.ARG_SERVER_ID))
        }
        composable(
            route = Routes.ONBOARDING,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition },
        ) {
            OnboardingView()
        }
        composable(
            route = Routes.ROUTE_CPU_DETAILS,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition },
        ) {
            CpuDetails()
        }
        composable(
            route = Routes.ROUTE_MEMORY_DETAILS,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition },
        ) {
            MemoryDetails()
        }
        composable(
            route = Routes.ROUTE_STORAGE_DETAILS,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition },
        ) {
            StorageDetails()
        }
        composable(
            route = Routes.ROUTE_NETWORK_DETAILS,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition },
        ) {
            NetworkDetails()
        }
    }
}