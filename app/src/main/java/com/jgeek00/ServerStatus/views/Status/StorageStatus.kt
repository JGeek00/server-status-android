package com.jgeek00.ServerStatus.views.Status

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.jgeek00.ServerStatus.models.Storage
import com.jgeek00.ServerStatus.utils.formatStorage

@Composable
fun StorageCard(values: List<Storage>) {
    val total = values.mapNotNull { v -> v.total }.sum()
    val available = values.mapNotNull { v -> v.available }.sum()
    val used = total - available
    val perc = (used/total)*100.0
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
                    painter = painterResource(id = R.drawable.storage_icon),
                    contentDescription = stringResource(R.string.storage),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier
                        .size(50.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.storage),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatStorage(total)
                    )
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Gauge(
                    value = "${perc.toInt()}%",
                    colors = gaugeColors,
                    percentage = perc,
                    size = 100.dp,
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.storage_icon),
                            contentDescription = stringResource(R.string.storage),
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                )
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Row {
                        Text(values.size.toString(), fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.volume))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text(formatStorage(available.toDouble()), fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.available))
                    }
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