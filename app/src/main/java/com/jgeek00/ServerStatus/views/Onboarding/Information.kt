package com.jgeek00.ServerStatus.views.Onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.constants.Urls
import com.jgeek00.ServerStatus.utils.openUrl
import com.jgeek00.ServerStatus.viewmodels.OnboardingViewModel

@Composable
fun Information() {
    val context = LocalContext.current

    val onboardingViewModel = viewModel<OnboardingViewModel>()

    Column(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Text(
                text = stringResource(R.string.information),
                fontSize = 40.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(24.dp))
            Text(
                stringResource(R.string.server_status_does_not_work_as_a_standalone_app_it_relies_on_a_service_that_is_deployed_the_server_to_get_the_data_in_order_to_use_this_application_you_must_deploy_status_a_server_monitoring_service_to_your_server_check_the_instructions_by_tapping_on_the_button_below),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                OutlinedButton(
                    onClick = {
                        openUrl(context, Urls.STATUS_DEPLOYMENT_INSTRUCTIONS)
                    }
                ) {
                    Text(stringResource(R.string.status_deployment_instructions))
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    onboardingViewModel.previousPage()
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = stringResource(R.string.back)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.back)
                    )
                }
            }
            Button(
                onClick = {
                    onboardingViewModel.finish()
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Check,
                        contentDescription = stringResource(R.string.finish)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.finish)
                    )
                }
            }
        }
    }
}