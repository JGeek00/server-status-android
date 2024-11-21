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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jgeek00.ServerStatus.constants.DataStoreKeys
import com.jgeek00.ServerStatus.di.DataStoreServiceEntryPoint
import com.jgeek00.ServerStatus.views.Onboarding.OnboardingView
import com.jgeek00.ServerStatus.views.ServerFormView
import com.jgeek00.ServerStatus.views.Settings.SettingsView
import com.jgeek00.ServerStatus.views.Status.StatusView
import dagger.hilt.android.EntryPointAccessors

@Composable
fun AppNavigation() {
    val context = LocalContext.current

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

    val dataStoreService = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            DataStoreServiceEntryPoint::class.java
        ).dataStoreService
    }

    val onboardingCompleted = dataStoreService.getBoolean(DataStoreKeys.ONBOARDING_COMPLETED).collectAsState(initial = false).value


    NavHost(
        navController = navigationController,
        startDestination = if (onboardingCompleted == true) Routes.ROUTE_STATUS else Routes.ONBOARDING
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
        ) {
            OnboardingView()
        }
    }
}