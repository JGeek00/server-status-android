package com.jgeek00.ServerStatus.views.Status.Details

import android.content.res.Configuration
import androidx.compose.animation.core.snap
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jgeek00.ServerStatus.R
import com.jgeek00.ServerStatus.components.ListTile
import com.jgeek00.ServerStatus.components.SectionHeader
import com.jgeek00.ServerStatus.di.StatusRepositoryEntryPoint
import com.jgeek00.ServerStatus.extensions.padEnd
import com.jgeek00.ServerStatus.models.StatusResult
import com.jgeek00.ServerStatus.navigation.NavigationManager
import dagger.hilt.android.EntryPointAccessors
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties
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
    var previousTx by remember { mutableStateOf<List<Double>>(emptyList()) }
    var previousRx by remember { mutableStateOf<List<Double>>(emptyList()) }

    var txChart by remember { mutableStateOf<List<Double>>(emptyList()) }
    var rxChart by remember { mutableStateOf<List<Double>>(emptyList()) }

    LaunchedEffect(data) {
        if (data.size <= 1) return@LaunchedEffect

        val previous = data.get(data.size-2)
        val current = data.last()

        if (previous.network?.tx == null || previous.network.rx == null) return@LaunchedEffect
        if (current.network?.tx == null || current.network.rx == null) return@LaunchedEffect

        val currentTx = current.network.tx.toDouble().minus(previous.network.tx.toDouble())/1000
        val currentRx = current.network.rx.toDouble().minus(previous.network.rx.toDouble())/1000

        val newTx = listOf(currentTx) + previousTx
        val newRx = listOf(currentRx) + previousRx

        txChart = if (newTx.size < 20) newTx.padEnd(20, 0.0) else newTx.take(20)
        rxChart = if (newRx.size < 20) newRx.padEnd(20, 0.0) else newRx.take(20)

        previousTx = newTx
        previousRx = newRx
    }

    LineChart(
        modifier = Modifier
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 32.dp)
            .height(400.dp),
        data = listOf(
            Line(
                label = "TX (Kbit/s)",
                values = txChart,
                color = SolidColor(MaterialTheme.colorScheme.primary),
                drawStyle = DrawStyle.Stroke(width = 2.dp),
                firstGradientFillColor = SolidColor(MaterialTheme.colorScheme.primary).value.copy(alpha = 0.75f),
                secondGradientFillColor = Color.Transparent,
                gradientAnimationDelay = 0,
                gradientAnimationSpec = snap(),
                strokeAnimationSpec = snap(),
                curvedEdges = true,
                popupProperties = PopupProperties(
                    enabled = true,
                    animationSpec = snap(),
                    duration = 0,
                    mode = PopupProperties.Mode.PointMode(),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        fontWeight = FontWeight.SemiBold
                    ),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentBuilder = { _, _, value -> String.format("%.2f", value) },
                    contentVerticalPadding = 6.dp,
                    contentHorizontalPadding = 12.dp,
                    cornerRadius = 6.dp
                )
            ),
            Line(
                label = "RX (Kbit/s)",
                values = rxChart,
                color = SolidColor(MaterialTheme.colorScheme.secondary),
                drawStyle = DrawStyle.Stroke(width = 2.dp),
                firstGradientFillColor = SolidColor(MaterialTheme.colorScheme.secondary).value.copy(alpha = 0.75f),
                secondGradientFillColor = Color.Transparent,
                gradientAnimationDelay = 0,
                gradientAnimationSpec = snap(),
                strokeAnimationSpec = snap(),
                curvedEdges = true,
                popupProperties = PopupProperties(
                    enabled = true,
                    animationSpec = snap(),
                    duration = 0,
                    mode = PopupProperties.Mode.PointMode(),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    ),
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentBuilder = { _, _, value -> String.format("%.2f", value) },
                    contentVerticalPadding = 6.dp,
                    contentHorizontalPadding = 12.dp,
                    cornerRadius = 6.dp
                )
            )
        ),
        animationDelay = 0,
        curvedEdges = false,
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            contentBuilder = { String.format("%.2f", it) },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface
            )
        ),
        labelHelperProperties = LabelHelperProperties(
            enabled = true,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    )
}