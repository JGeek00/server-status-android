package com.jgeek00.ServerStatus.views.Status

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
import com.jgeek00.ServerStatus.navigation.enterTransition
import com.jgeek00.ServerStatus.navigation.exitTransition
import com.jgeek00.ServerStatus.navigation.popEnterTransition
import com.jgeek00.ServerStatus.navigation.popExitTransition
import com.jgeek00.ServerStatus.utils.createServerAddress
import com.jgeek00.ServerStatus.utils.formatBits
import com.jgeek00.ServerStatus.utils.formatBytes
import com.jgeek00.ServerStatus.utils.formatMemory
import com.jgeek00.ServerStatus.utils.formatStorage
import com.jgeek00.ServerStatus.views.Status.Details.CpuDetails
import com.jgeek00.ServerStatus.views.Status.Details.MemoryDetails
import com.jgeek00.ServerStatus.views.Status.Details.NetworkDetails
import com.jgeek00.ServerStatus.views.Status.Details.StorageDetails
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.Route
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

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val servers = serverInstancesRepository.servers.collectAsState(initial = emptyList())

    val navigationController = rememberNavController()

    fun replaceRoute(route: String) {
        navigationController.popBackStack()
        navigationController.navigate(route)
        NavigationManager.getInstance().clearNavEvent()
    }

    if (servers.value.isNotEmpty()) {
        BoxWithConstraints {
            val width = constraints.maxWidth

            if (width > 1500) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                    ) {
                        GeneralStatus(
                            viewCpuDetails = { replaceRoute(Routes.ROUTE_CPU_DETAILS) },
                            viewMemoryDetails = { replaceRoute(Routes.ROUTE_MEMORY_DETAILS) },
                            viewStorageDetails = { replaceRoute(Routes.ROUTE_STORAGE_DETAILS) },
                            viewNetworkDetails = { replaceRoute(Routes.ROUTE_NETWORK_DETAILS) }
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.6f)
                    ) {
                        NavHost(
                            navController = navigationController,
                            startDestination = Routes.ROUTE_EMPTY
                        ) {
                            composable(
                                route = Routes.ROUTE_EMPTY,
                            ) {
                                Box {}
                            }
                            composable(
                                route = Routes.ROUTE_CPU_DETAILS,
                            ) {
                                CpuDetails(true)
                            }
                            composable(
                                route = Routes.ROUTE_MEMORY_DETAILS,
                            ) {
                                MemoryDetails(true)
                            }
                            composable(
                                route = Routes.ROUTE_STORAGE_DETAILS,
                            ) {
                                StorageDetails(true)
                            }
                            composable(
                                route = Routes.ROUTE_NETWORK_DETAILS,
                            ) {
                                NetworkDetails(true)
                            }
                        }
                    }
                }
            }
            else {
                GeneralStatus(
                    viewCpuDetails = { NavigationManager.getInstance().navigateTo(Routes.ROUTE_CPU_DETAILS) },
                    viewMemoryDetails = { NavigationManager.getInstance().navigateTo(Routes.ROUTE_MEMORY_DETAILS) },
                    viewStorageDetails = { NavigationManager.getInstance().navigateTo(Routes.ROUTE_STORAGE_DETAILS) },
                    viewNetworkDetails = { NavigationManager.getInstance().navigateTo(Routes.ROUTE_NETWORK_DETAILS) }
                )
            }
        }
    }
    else {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            topBar = {
                LargeTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
                    scrollBehavior = scrollBehavior,
                    title = {
                        Text(text = stringResource(R.string.status))
                    },
                    actions = {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
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
            val displayCutout = if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) WindowInsets.displayCutout else WindowInsets(0.dp)
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .windowInsetsPadding(displayCutout)
                    .fillMaxSize()
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

