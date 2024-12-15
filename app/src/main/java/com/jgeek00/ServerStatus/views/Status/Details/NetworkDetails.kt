package com.jgeek00.ServerStatus.views.Status.Details

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.ListTile
import com.jgeek00.ServerStatus.components.SectionHeader
import com.jgeek00.ServerStatus.di.StatusRepositoryEntryPoint
import com.jgeek00.ServerStatus.extensions.padEnd
import com.jgeek00.ServerStatus.models.StatusResult
import com.jgeek00.ServerStatus.navigation.NavigationManager
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import dagger.hilt.android.EntryPointAccessors
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkDetails(tabletMode: Boolean) {
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
                        text = stringResource(R.string.network)
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
            if (last?.network != null) {
                item {
                    SectionHeader(title = stringResource(R.string.information))
                    if (last.network.networkInterface != null) {
                        ListTile(
                            label = stringResource(R.string.iface),
                            supportingText = last.network.networkInterface
                        )
                    }
                    if (last.network.speed != null) {
                        val formatted = String.format(
                            Locale.getDefault(),
                            "%.1f",
                            last.network.speed.toDouble() / 1000.0
                        )
                        ListTile(
                            label = stringResource(R.string.speed),
                            supportingText = "$formatted Gbit/s"

                        )
                    }
                }
                item {
                    SectionHeader(title = stringResource(R.string.data_transfer))
                    NetworkChart(values)
                }
            }
        }
    }
}

@Composable
private fun NetworkChart(data: List<StatusResult>) {
    val modelProducer = remember { CartesianChartModelProducer() }

    var previousTx by remember { mutableStateOf<List<Double>>(emptyList()) }
    var previousRx by remember { mutableStateOf<List<Double>>(emptyList()) }

    LaunchedEffect(data) {
        if (data.size <= 1) return@LaunchedEffect

        val previous = data.get(data.size-2)
        val current = data.last()

        if (previous.network?.tx == null || previous.network.rx == null) return@LaunchedEffect
        if (current.network?.tx == null || current.network.rx == null) return@LaunchedEffect

        val currentTx = current.network.tx.toDouble().minus(previous.network.tx.toDouble())/1000
        val currentRx = current.network.rx.toDouble().minus(previous.network.rx.toDouble())/1000

        val newTx = (previousTx + currentTx)
        val newRx = previousRx + currentRx

        val txChart = if (newTx.size < 20) newTx.padEnd(20, 0f) else newTx.takeLast(20)
        val rxChart = if (newRx.size < 20) newRx.padEnd(20, 0f) else newRx.takeLast(20)

        modelProducer.runTransaction {
            lineSeries { series(txChart) }
            lineSeries { series(rxChart) }
        }

        previousTx = newTx
        previousRx = newRx
    }

    Column {
        CartesianChartHost(
            chart = rememberCartesianChart(
                layers = arrayOf(
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(
                                fill = LineCartesianLayer.LineFill.single(fill(MaterialTheme.colorScheme.primary)),
                                areaFill = LineCartesianLayer.AreaFill.single(fill(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))),
                            )
                        )
                    ),
                    rememberLineCartesianLayer(
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(
                                fill = LineCartesianLayer.LineFill.single(fill(MaterialTheme.colorScheme.secondary)),
                                areaFill = LineCartesianLayer.AreaFill.single(fill(MaterialTheme.colorScheme.secondary.copy(alpha = 0.25f))),
                            )
                        )
                    ),
                ),
                startAxis = VerticalAxis.rememberStart(),
                bottomAxis = HorizontalAxis.rememberBottom(
                    label = null,
                    guideline = null,
                    tickLength = 0.dp
                ),
                marker = rememberMarker()
            ),
            modelProducer = modelProducer,
            animationSpec = null,
            scrollState = rememberVicoScrollState(
                scrollEnabled = false
            ),
            modifier = Modifier
                .padding(horizontal = 16.dp)
        )
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp, start = 24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "TX (Kbit/s)",
                fontSize = 12.sp
            )
            Spacer(Modifier.width(24.dp))
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "RX (Kbit/s)",
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun rememberMarker(): DefaultCartesianMarker {
    val label = rememberTextComponent(
        padding = Dimensions(horizontalDp = 8f, verticalDp = 4f),
        background = ShapeComponent(
            fill = Fill(MaterialTheme.colorScheme.primaryContainer.toArgb()),
            shadow = Shadow(
                radiusDp = 4f,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.4f).toArgb()
            ),
            shape = CorneredShape.Pill
        ),
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        typeface = android.graphics.Typeface.DEFAULT_BOLD,
        margins = Dimensions(bottomDp = 8f)
    )
    val indicator = rememberShapeComponent(
        fill = Fill(MaterialTheme.colorScheme.primary.toArgb()),
        strokeThickness = 8.dp,
        shape = CorneredShape.Pill
    )

    return DefaultCartesianMarker(
        label = label,
        labelPosition = DefaultCartesianMarker.LabelPosition.AbovePoint,
        indicator = { _ -> indicator },
    )
}