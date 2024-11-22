package com.jgeek00.ServerStatus.views.Onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jgeek00.ServerStatus.viewmodels.OnboardingViewModel

@Composable
fun OnboardingView() {
    val onboardingViewModel = hiltViewModel<OnboardingViewModel>()

    val currentPage = onboardingViewModel.currentPage.collectAsState(initial = 0).value

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { 2 }
    )

    LaunchedEffect(currentPage) {
        pagerState.animateScrollToPage(currentPage)
    }

    Column(
        modifier = Modifier
            .safeDrawingPadding()
    ) {
        HorizontalPager(
            state = pagerState,
            userScrollEnabled = false,
            modifier = Modifier
                .weight(1f)
        ) { page ->
            when (page) {
                0 -> Welcome()
                1 -> Information()
            }
        }
    }
}