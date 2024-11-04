package com.jgeek00.ServerStatus.views.Status

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
import com.jgeek00.ServerStatus.utils.createServerAddress
import com.jgeek00.ServerStatus.utils.formatBits
import com.jgeek00.ServerStatus.utils.formatBytes
import com.jgeek00.ServerStatus.utils.formatMemory
import com.jgeek00.ServerStatus.utils.formatStorage
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
                            .padding(bottom = 8.dp)
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
