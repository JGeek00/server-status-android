package com.jgeek00.ServerStatus.views.Status

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.Gauge
import com.jgeek00.ServerStatus.constants.gaugeColors
import com.jgeek00.ServerStatus.models.CPU
import com.jgeek00.ServerStatus.navigation.NavigationManager
import com.jgeek00.ServerStatus.navigation.Routes

@Composable
fun CpuCard(values: CPU, viewDetails: () -> Unit) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    val gaugeWidth = if ((screenWidthDp/2 - 64.dp) <= 100.dp) screenWidthDp/2 - 64.dp else 100.dp

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.Memory,
                contentDescription = stringResource(R.string.cpu),
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.cpu),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                values.model?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        fontSize = 14.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            values.utilisation?.let {
                Gauge(
                    value = "${(it*100).toInt()}%",
                    colors = gaugeColors,
                    percentage = it*100,
                    size = gaugeWidth,
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Memory,
                            contentDescription = stringResource(R.string.cpu),
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
            values.cpuCores?.let {
                val maxTemp = ((it.map { core -> core.temperatures?.first() }.filter { temp -> temp != null }) as List<Long>).max()
                Gauge(
                    value = "$maxTemp ºC",
                    colors = gaugeColors,
                    percentage = maxTemp.toDouble(),
                    size = gaugeWidth,
                    icon = {
                        Icon(
                            imageVector = Icons.Rounded.Thermostat,
                            contentDescription = stringResource(R.string.temperature),
                            modifier = Modifier.size(36.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = {
                    viewDetails()
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.view_more))
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = stringResource(R.string.view_more)
                    )
                }
            }
        }
    }
}