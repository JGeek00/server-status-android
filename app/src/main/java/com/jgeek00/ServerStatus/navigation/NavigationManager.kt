package com.jgeek00.ServerStatus.navigation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jgeek00.ServerStatus.views.SettingsView
import com.jgeek00.ServerStatus.views.StatusView

@Composable
fun NavigationManager() {
    val navigationController = rememberNavController()

    val slideTime = 400

    // https://cubic-bezier.com/#.55,0,0,1
    // val easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f) -> md.sys.motion.easing.emphasized.decelerate
    val easing = CubicBezierEasing(0.55f, 0.0f, 0.0f, 1f)

    val enterTransition = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(slideTime, easing = easing)) + fadeIn(animationSpec = tween(slideTime, easing = easing))
    val exitTransition = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(slideTime, easing = easing)) + fadeOut(animationSpec = tween(slideTime, easing = easing))

    val popExitTransition = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(slideTime, easing = easing)) + fadeOut(animationSpec = tween(slideTime, easing = easing))
    val popEnterTransition = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(slideTime, easing = easing)) + fadeIn(animationSpec = tween(slideTime, easing = easing))

    NavHost(
        navController = navigationController,
        startDestination = Routes.STATUS
    ) {
        composable(
            route = Routes.STATUS,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            StatusView(navigationController)
        }
        composable(
            route = Routes.SETTINGS,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            SettingsView(navigationController)
        }
    }
}