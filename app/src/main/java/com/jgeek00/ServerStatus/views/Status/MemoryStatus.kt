package com.jgeek00.ServerStatus.views.Status

import androidx.compose.foundation.Image
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.Gauge
import com.jgeek00.ServerStatus.constants.gaugeColors
import com.jgeek00.ServerStatus.models.Memory
import com.jgeek00.ServerStatus.utils.formatMemory

@Composable
fun MemoryCard(values: Memory) {
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
                Image(
                    painter = painterResource(id = R.drawable.memory_icon),
                    contentDescription = stringResource(R.string.memory),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier
                        .size(50.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.memory_ram),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    values.total?.let {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${formatMemory(it)} GB"
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
                if (values.total != null && values.available != null) {
                    val used = values.total - values.available
                    val perc = (used.toDouble()/values.total.toDouble())*100.0
                    Gauge(
                        value = "${perc.toInt()}%",
                        colors = gaugeColors,
                        percentage = perc,
                        size = 100.dp,
                        icon = {
                            Image(
                                painter = painterResource(id = R.drawable.memory_icon),
                                contentDescription = stringResource(R.string.memory),
                                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurfaceVariant),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val used = values.available?.let { values.total?.minus(it) }
                    val swapUsed = values.swap_available?.let { values.swap_total?.minus(it) }
                    Row {
                        Text("${formatMemory(values.available)} GB", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.available))
                    }
                    Row {
                        Text("${formatMemory(used)} GB", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.in_use))
                    }
                    Spacer(Modifier.height(4.dp))
                    Row {
                        Text("${formatMemory(swapUsed)} GB", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.in_swap), fontSize = 12.sp)
                    }
                    Row {
                        Text("${formatMemory(values.cached)} GB", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.in_cache), fontSize = 12.sp)
                    }
                }
            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Row(
//                horizontalArrangement = Arrangement.End,
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//                Button(
//                    onClick = {}
//                ) {
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically
//                    ) {
//                        Text(stringResource(R.string.view_more))
//                        Spacer(modifier = Modifier.width(6.dp))
//                        Icon(
//                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
//                            contentDescription = stringResource(R.string.view_more)
//                        )
//                    }
//                }
//            }
        }
    }
}