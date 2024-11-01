package com.jgeek00.ServerStatus.views.Status

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.Gauge
import com.jgeek00.ServerStatus.constants.gaugeColors
import com.jgeek00.ServerStatus.models.CPU

@Composable
fun CpuCard(values: CPU) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
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
                    modifier = Modifier.size(50.dp),
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.cpu),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    values.model?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = it
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
                        size = 100.dp,
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Memory,
                                contentDescription = stringResource(R.string.cpu),
                                modifier = Modifier.size(40.dp),
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
                        size = 100.dp,
                        icon = {
                            Icon(
                                imageVector = Icons.Rounded.Thermostat,
                                contentDescription = stringResource(R.string.temperature),
                                modifier = Modifier.size(40.dp),
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
                    onClick = {}
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
}