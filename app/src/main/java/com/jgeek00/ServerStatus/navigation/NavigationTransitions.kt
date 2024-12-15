package com.jgeek00.ServerStatus.navigation

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

const val slideTime = 500

// https://cubic-bezier.com/#.55,0,0,1
val easing = CubicBezierEasing(0.2f, 0.7f, 0.1f, 1f)
// val easing = CubicBezierEasing(0.55f, 0.0f, 0.0f, 1f)

val enterTransition = slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(slideTime, easing = easing)) + fadeIn(animationSpec = tween(slideTime, easing = easing))
val exitTransition = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(slideTime, easing = easing)) + fadeOut(animationSpec = tween(slideTime, easing = easing))

val popExitTransition = slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(slideTime, easing = easing)) + fadeOut(animationSpec = tween(slideTime, easing = easing))
val popEnterTransition = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(slideTime, easing = easing)) + fadeIn(animationSpec = tween(slideTime, easing = easing))