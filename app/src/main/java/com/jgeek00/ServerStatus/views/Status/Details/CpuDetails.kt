package com.jgeek00.ServerStatus.views.Status.Details

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.remember
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
import com.jgeek00.ServerStatus.components.ListTile
import com.jgeek00.ServerStatus.components.SectionHeader
import com.jgeek00.ServerStatus.di.StatusRepositoryEntryPoint
import com.jgeek00.ServerStatus.extensions.padEnd
import com.jgeek00.ServerStatus.models.StatusResult
import com.jgeek00.ServerStatus.navigation.NavigationManager
import com.jgeek00.ServerStatus.utils.cacheValue
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CpuDetails(tabletMode: Boolean) {
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
                        text = stringResource(R.string.cpu)
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

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .windowInsetsPadding(displayCutout)
                .padding(padding)
        ) {
            if (last?.cpu?.cpuCores != null) {
                val maxTemp =
                    last.cpu.cpuCores.mapNotNull { if (!it.temperatures.isNullOrEmpty()) it.temperatures[0] else 0.0 }
                        .max()

                item {
                    SectionHeader(title = stringResource(R.string.information))
                    if (last.cpu.model != null) {
                        ListTile(
                            label = stringResource(R.string.model),
                            supportingText = last.cpu.model
                        )
                    }
                    if (last.cpu.cores != null && last.cpu.count != null) {
                        ListTile(
                            label = stringResource(R.string.cores),
                            supportingText = stringResource(
                                R.string.physical_cores_execution_threads,
                                last.cpu.cores,
                                last.cpu.count
                            )
                        )
                    }
                    if (last.cpu.cache != null) {
                        ListTile(
                            label = stringResource(R.string.cache),
                            supportingText = cacheValue(last.cpu.cache)
                        )
                    }
                    SectionHeader(title = stringResource(R.string.general_status))
                    if (last.cpu.utilisation != null) {
                        ListTile(
                            label = stringResource(R.string.load),
                            supportingText = "${(last.cpu.utilisation * 100).toInt()}%"
                        )
                    }
                    ListTile(
                        label = stringResource(R.string.temperature),
                        supportingText = "${maxTemp.toInt()}°C"
                    )
                }
                items(last.cpu.cpuCores.size) { coreIndex ->
                    CpuCoreCharts(data = values, coreIndex = coreIndex)
                }
            }
        }
    }
}

@Composable
private fun CpuCoreCharts(data: List<StatusResult>, coreIndex: Int) {
    val freqsModelProducer = remember { CartesianChartModelProducer() }
    val tempsModelProducer = remember { CartesianChartModelProducer() }

    if (data.isNotEmpty()) {
        val sliced = if (data.size > 20) data.reversed().slice(0..19) else data.reversed()

        val coreFreqs = sliced.mapNotNull { it.cpu?.cpuCores?.get(coreIndex)?.frequencies }
        val maxFreq = coreFreqs.maxOfOrNull { it.max ?: 0 }?.toDouble()
        val freqsValues = coreFreqs.map { (it.now ?: 0).toFloat() }
        val freqsChart = if (freqsValues.size < 20) freqsValues.padEnd(20, 0f) else freqsValues

        val coreTemps = sliced.mapNotNull { it.cpu?.cpuCores?.get(coreIndex)?.temperatures }
        val maxTemp = coreTemps.flatten().filterNotNull().maxOrNull()
        val tempsValues = coreTemps.map {
            val filtered = it.filterNotNull()
            if (filtered.isNotEmpty()) filtered[0]
            else 0
        }
        val tempsChart = if (tempsValues.size < 20) tempsValues.padEnd(20, 0f) else tempsValues


        LaunchedEffect(freqsChart) {
            freqsModelProducer.runTransaction { lineSeries { series(freqsChart) } }
            tempsModelProducer.runTransaction { lineSeries { series(tempsChart) } }
        }

        Column {
            SectionHeader(
                title = stringResource(R.string.core, coreIndex + 1),
                modifier = Modifier
                    .padding(top = 0.dp, bottom = 0.dp, start = 16.dp)
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.frequency_mhz),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            LineChart(
                modelProducer = freqsModelProducer,
                range = ChartRange(min = 0.0, max = maxFreq ?: 0.0),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.temperature_c),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            LineChart(
                modelProducer = tempsModelProducer,
                range = ChartRange(min = 0.0, max = maxTemp ?: 0.0),
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )
        }
    }
}