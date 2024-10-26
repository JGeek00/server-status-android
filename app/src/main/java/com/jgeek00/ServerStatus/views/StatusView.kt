package com.jgeek00.ServerStatus.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.Gauge
import com.jgeek00.ServerStatus.constants.gaugeColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun StatusView(navigationController: NavHostController) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var refreshing by remember { mutableStateOf(false) }
    val state = rememberPullRefreshState(refreshing, {
        refreshing = true
    })

    LaunchedEffect(refreshing) {
        if (refreshing) {
            withContext(Dispatchers.IO) {
                delay(1500) // Fake network delay
                refreshing = false
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = stringResource(R.string.status))
                        Text(
                            text = "https://status.server.com",
                            color = Color.Gray,
                            fontSize = 13.sp,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { dropdownExpanded = true }) {
                        Icon(
                            imageVector = Icons.Rounded.MoreVert,
                            contentDescription = "Menu"
                        )
                    }
                    DropdownMenu(
                        onDismissRequest = { dropdownExpanded = false },
                        expanded = dropdownExpanded
                    ) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Settings,
                                    contentDescription = "Settings"
                                )
                            },
                            text = { Text(stringResource(R.string.settings)) },
                            onClick = { navigationController.navigate("/settings") }
                        )
                    }
                },
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(state)
                .padding(padding)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .verticalScroll(rememberScrollState())
            ) {
                CpuCard()
                MemoryCard()
                StorageCard()
                NetworkCard()
            }
            PullRefreshIndicator(
                refreshing = refreshing,
                state = state,
                modifier = Modifier
                    .align(alignment = Alignment.TopCenter),
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CpuCard() {
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
                    painter = painterResource(id = R.drawable.cpu_icon),
                    contentDescription = stringResource(R.string.cpu),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier
                        .size(50.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.cpu),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Intel N100"
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
                    value = "20%",
                    colors = gaugeColors,
                    percentage = 20.0,
                    size = 100.dp,
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.cpu_icon),
                            contentDescription = stringResource(R.string.cpu),
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                )
                Gauge(
                    value = "50ºC",
                    colors = gaugeColors,
                    percentage = 50.0,
                    size = 100.dp,
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.thermostat_icon),
                            contentDescription = stringResource(R.string.temperature),
                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.size(40.dp)
                        )
                    }
                )
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

@Composable
fun MemoryCard() {
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "16 GB"
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
                    value = "30%",
                    colors = gaugeColors,
                    percentage = 30.0,
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row {
                        Text("10 GB", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.available))
                    }
                    Row {
                        Text("6 GB", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.in_use))
                    }
                    Spacer(Modifier.height(4.dp))
                    Row {
                        Text("0 GB", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.in_swap), fontSize = 12.sp)
                    }
                    Row {
                        Text("8 GB", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.in_cache), fontSize = 12.sp)
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

@Composable
fun StorageCard() {
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
                        text = "500 GB"
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
                    value = "70%",
                    colors = gaugeColors,
                    percentage = 70.0,
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
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row {
                        Text("1", fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.volume))
                    }
                    Row {
                        Text("450 GB", fontWeight = FontWeight.SemiBold)
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

@Composable
fun NetworkCard() {
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
                    painter = painterResource(id = R.drawable.network_icon),
                    contentDescription = stringResource(R.string.network),
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier
                        .size(50.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = stringResource(R.string.network),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "1 Gbit/s"
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.download_traffic),
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "10 Mbit/s"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "10 MB/s"
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.upload_traffic),
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "10 Mbit/s"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "10 MB/s"
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
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