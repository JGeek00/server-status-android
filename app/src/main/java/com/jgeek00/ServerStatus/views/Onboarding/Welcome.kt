package com.jgeek00.ServerStatus.views.Onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.viewmodels.OnboardingViewModel

@Composable
fun Welcome() {
    val onboardingViewModel = viewModel<OnboardingViewModel>()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.icon),
                contentDescription = "App icon",
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(100.dp))
            )
            Spacer(Modifier.height(30.dp))
            Text(
                stringResource(R.string.welcome_to_server_status),
                fontSize = 40.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 50.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(30.dp))
            Text(
                stringResource(R.string.an_app_to_check_the_status_of_your_home_server_hardware),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp
            )
        }
        Button(
            onClick = {
                onboardingViewModel.nextPage()
            }
        ) {
            Text(stringResource(R.string.get_stated))
        }
    }
}