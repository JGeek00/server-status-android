package com.jgeek00.ServerStatus.views.Status.Details

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.ChartRange
import com.jgeek00.ServerStatus.components.LineChart
import com.jgeek00.ServerStatus.components.LineChart2
import com.jgeek00.ServerStatus.components.ListTile
import com.jgeek00.ServerStatus.components.SectionHeader
import com.jgeek00.ServerStatus.di.StatusRepositoryEntryPoint
import com.jgeek00.ServerStatus.models.StatusResult
import com.jgeek00.ServerStatus.navigation.NavigationManager
import com.jgeek00.ServerStatus.utils.cacheValue
import com.jgeek00.ServerStatus.utils.formatMemory
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import dagger.hilt.android.EntryPointAccessors
import androidx.compose.runtime.remember as remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryDetails(tabletMode: Boolean) {
    val context = LocalContext.current

    val statusRepository = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            StatusRepositoryEntryPoint::class.java
        ).statusRepository
    }

    val values = statusRepository.data.collectAsState(initial = emptyList()).value
    val last = if (values.isNotEmpty()) values.last() else null

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                ),
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(R.string.memory)
                    )
                },
                navigationIcon = {
                    if (!tabletMode) {
                        IconButton(
                            onClick = {
                                NavigationManager.getInstance().popBack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        val displayCutout =
            if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) WindowInsets.displayCutout else WindowInsets(
                0.dp
            )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .windowInsetsPadding(
                    if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) displayCutout else WindowInsets(
                        0.dp
                    )
                )
                .padding(padding)
        ) {
            if (last?.memory != null) {
                SectionHeader(
                    title = stringResource(R.string.general_status),
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
                )
                if (last.memory.total != null) {
                    ListTile(
                        label = stringResource(R.string.total),
                        supportingText = formatMemory(last.memory.total)
                    )
                }
                if (last.memory.available != null && last.memory.total != null) {
                    val used = last.memory.total - last.memory.available
                    ListTile(
                        label = stringResource(R.string.In_use),
                        supportingText = formatMemory(used)
                    )
                }
                if (last.memory.available != null) {
                    ListTile(
                        label = stringResource(R.string.Available),
                        supportingText = formatMemory(last.memory.available)
                    )
                }
                if (last.memory.cached != null) {
                    ListTile(
                        label = stringResource(R.string.In_cache),
                        supportingText = formatMemory(last.memory.cached)
                    )
                }
                if (last.memory.swap_total != null) {
                    ListTile(
                        label = stringResource(R.string.swap_total),
                        supportingText = formatMemory(last.memory.swap_total)
                    )
                }
                if (last.memory.swap_available != null) {
                    ListTile(
                        label = stringResource(R.string.swap_available),
                        supportingText = formatMemory(last.memory.swap_available)
                    )
                }
                SectionHeader(
                    title = stringResource(R.string.usage),
                    modifier = Modifier.padding(start = 16.dp, top = 32.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.memory_gb),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                    )
                }
                MemoryChart(data = values)
            }
        }
    }
}

@Composable
private fun MemoryChart(data: List<StatusResult>) {
    val maxValue = data.mapNotNull { if (it.memory?.total != null) it.memory.total.toDouble() else null }.max()

    val slicedValues = if (data.size > 20) data.reversed().slice(0..19) else data.reversed()
    val values = slicedValues.mapNotNull { if (it.memory?.total != null && it.memory.available != null) it.memory.total - it.memory.available else null }
    val chartValues = values.map { it.toDouble()/1048576 }

    LineChart2(
        modifier = Modifier
            .height(300.dp)
            .padding(16.dp),
        values = chartValues,
        color = MaterialTheme.colorScheme.primary,
        secondaryColor = MaterialTheme.colorScheme.primaryContainer,
        maxValue = maxValue/1048576,
        minValue = 0.0,
        tooltipFormatter = { _, _, value -> String.format("%.2f", value) },
        axisFormatter = { String.format("%.2f", it) }
    )
}