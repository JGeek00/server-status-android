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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.Gauge
import com.jgeek00.ServerStatus.constants.gaugeColors
import com.jgeek00.ServerStatus.models.Memory
import com.jgeek00.ServerStatus.utils.formatMemory

@Composable
fun MemoryCard(values: Memory) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.memory_icon),
                contentDescription = stringResource(R.string.memory),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = stringResource(R.string.memory_ram),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                values.total?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${formatMemory(it)} GB",
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
            if (values.total != null && values.available != null) {
                val used = values.total - values.available
                val perc = (used.toDouble()/values.total.toDouble())*100.0
                Gauge(
                    value = "${perc.toInt()}%",
                    colors = gaugeColors,
                    percentage = perc,
                    size = if ((screenWidthDp/2 - 64.dp) <= 100.dp) screenWidthDp/2 - 64.dp else 100.dp,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.memory_icon),
                            contentDescription = stringResource(R.string.memory),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                )
                Spacer(Modifier.width(24.dp))
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
                    Text(stringResource(R.string.available), fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Row {
                    Text("${formatMemory(used)} GB", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.in_use), fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(Modifier.height(4.dp))
                Row {
                    Text("${formatMemory(swapUsed)} GB", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.in_swap), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Row {
                    Text("${formatMemory(values.cached)} GB", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.in_cache), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
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