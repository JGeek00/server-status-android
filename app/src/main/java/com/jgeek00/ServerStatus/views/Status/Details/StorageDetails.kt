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
import com.jgeek00.ServerStatus.components.LineChart
import com.jgeek00.ServerStatus.components.ListTile
import com.jgeek00.ServerStatus.components.SectionHeader
import com.jgeek00.ServerStatus.di.StatusRepositoryEntryPoint
import com.jgeek00.ServerStatus.models.StatusResult
import com.jgeek00.ServerStatus.navigation.NavigationManager
import com.jgeek00.ServerStatus.utils.formatStorage
import dagger.hilt.android.EntryPointAccessors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageDetails(tabletMode: Boolean) {
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
                        text = stringResource(R.string.storage)
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
            if (last?.storage != null) {
                items(last.storage.size) { index ->
                    StorageItem(values, index)
                }
            }
        }
    }
}

@Composable
private fun StorageItem(data: List<StatusResult>, index: Int) {
    val last = data.last().storage?.get(index)

    Column {
        if (last != null) {
            if (last.name != null) {
                SectionHeader(title = last.name)
            }
            if (last.total != null) {
                ListTile(
                    label = stringResource(R.string.total),
                    supportingText = formatStorage(last.total)
                )
            }
            if (last.total != null && last.available != null) {
                val used = last.total - last.available
                ListTile(
                    label = stringResource(R.string.in_use),
                    supportingText = formatStorage(used)
                )
            }
            if (last.available != null) {
                ListTile(
                    label = stringResource(R.string.Available),
                    supportingText = formatStorage(last.available.toDouble())
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.in_use_gb),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                )
            }
            StorageChart(data, index)
        }
    }
}

@Composable
private fun StorageChart(data: List<StatusResult>, index: Int) {
    val maxValue = data.mapNotNull { if (it.memory?.total != null) it.storage?.get(index)?.total else null }.max()

    val slicedValues = if (data.size > 20) data.reversed().slice(0..19) else data.reversed()
    val values = slicedValues.mapNotNull { if (it.storage?.get(index)?.total != null && it.storage.get(index).available != null) it.storage.get(index).total!! - it.storage.get(index).available!! else null }
    val chartValues = values.map { it/1073741824 }

    LineChart(
        modifier = Modifier
            .height(300.dp)
            .padding(16.dp),
        values = chartValues,
        color = MaterialTheme.colorScheme.primary,
        secondaryColor = MaterialTheme.colorScheme.primaryContainer,
        maxValue = maxValue/1073741824,
        minValue = 0.0,
        tooltipFormatter = { _, _, value -> String.format("%.2f", value) },
        axisFormatter = { String.format("%.2f", it) }
    )
}