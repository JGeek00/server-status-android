package com.jgeek00.ServerStatus.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Dns
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Memory
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.Gauge
import com.jgeek00.ServerStatus.constants.gaugeColors
import com.jgeek00.ServerStatus.di.ServerInstancesRepositoryEntryPoint
import com.jgeek00.ServerStatus.di.StatusRepositoryEntryPoint
import com.jgeek00.ServerStatus.models.CPU
import com.jgeek00.ServerStatus.models.Memory
import com.jgeek00.ServerStatus.models.Network
import com.jgeek00.ServerStatus.models.Storage
import com.jgeek00.ServerStatus.navigation.NavigationManager
import com.jgeek00.ServerStatus.navigation.Routes
import com.jgeek00.ServerStatus.repository.ServerInstancesRepository
import com.jgeek00.ServerStatus.utils.createServerAddress
import com.jgeek00.ServerStatus.utils.formatBits
import com.jgeek00.ServerStatus.utils.formatBytes
import com.jgeek00.ServerStatus.utils.formatMemory
import com.jgeek00.ServerStatus.utils.formatStorage
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusView() {
    val context = LocalContext.current

    val serverInstancesRepository = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            ServerInstancesRepositoryEntryPoint::class.java
        ).serverInstancesRepository
    }

    val statusRepository = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            StatusRepositoryEntryPoint::class.java
        ).statusRepository
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    var refreshing by remember { mutableStateOf(false) }
    val state = rememberPullToRefreshState()

    val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)

    LaunchedEffect(refreshing) {
        if (refreshing) {
            coroutineScope.launch {
                statusRepository.refresh()
                refreshing = false
            }
        }
    }

    val values = statusRepository.data.collectAsState(initial = emptyList())
    val servers = serverInstancesRepository.servers.collectAsState(initial = emptyList())

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
                        statusRepository.selectedServer.value?.let { value ->
                            Text(
                                text = createServerAddress(value.method, value.ipDomain, value.port, value.path),
                                color = Color.Gray,
                                fontSize = 13.sp,
                            )
                        }
                    }
                },
                actions = {
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip { Text(stringResource(R.string.settings)) }
                        },
                        state = rememberTooltipState(),
                    ) {
                        IconButton(
                            onClick = { NavigationManager.getInstance().navigateTo(Routes.ROUTE_SETTINGS) },
                            modifier = Modifier
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                    }
                },
            )
        }
    ) { padding ->
        if (servers.value.isNotEmpty()) {
            if (statusRepository.loading.value) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = stringResource(R.string.loading_status),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 22.sp
                    )
                }
            }
            else if (statusRepository.error.value || values.value.isEmpty()) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Image(
                        imageVector = Icons.Rounded.Error,
                        contentDescription = stringResource(R.string.error),
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onSurfaceVariant),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(30.dp))
                    Text(
                        text = stringResource(R.string.an_error_occurred_when_loading_the_status),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 22.sp
                    )
                }
            }
            else {
                PullToRefreshBox(
                    state = state,
                    isRefreshing = refreshing,
                    onRefresh = { refreshing = true },
                    modifier = Modifier
                        .padding(padding)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                            .verticalScroll(rememberScrollState())
                    ) {
                        values.value.last().cpu?.let {
                            CpuCard(it)
                        }
                        values.value.last().memory?.let {
                            MemoryCard(it)
                        }
                        values.value.last().storage?.let {
                            StorageCard(it)
                        }
                        values.value.last().network?.let { v1 ->
                            val previous = if (values.value.size >= 2) values.value[values.value.size - 2].network else null
                            NetworkCard(current = v1, previous = previous)
                        }
                    }
                }
            }
        }
        else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.no_server_connections_created),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.create_a_connection_to_a_server_to_begin),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    onClick = { NavigationManager.getInstance().navigateTo(Routes.ROUTE_SERVER_FORM) }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Dns,
                            contentDescription = stringResource(R.string.create_server),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.create_server))
                    }
                }
            }
        }
    }
}

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

@Composable
fun NetworkCard(current: Network, previous: Network?) {
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
                        text = if (current.speed != null) {
                            val formatted = String.format(
                                Locale.getDefault(),
                                "%.1f",
                                current.speed.toDouble() / 1000.0
                            )
                            "$formatted Gbit/s"
                        } else "N/A"
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
                        text = if (previous != null) formatBits(previous.rx?.let { current.rx?.minus(it) }) else "0 Bit/s"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (previous != null) formatBytes(previous.rx?.let { current.rx?.minus(it) }) else "0 B/s"
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
                        text = if (previous != null) formatBits(previous.tx?.let { current.tx?.minus(it) }) else "0 Bit/s"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (previous != null) formatBytes(previous.tx?.let { current.tx?.minus(it) }) else "0 B/s"
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