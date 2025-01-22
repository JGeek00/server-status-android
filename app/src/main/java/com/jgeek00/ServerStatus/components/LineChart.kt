package com.jgeek00.ServerStatus.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Dimensions
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.component.Shadow
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.shape.CorneredShape

class ChartRange(val min: Double, val max: Double)

@Composable
fun LineChart(
    modelProducer: CartesianChartModelProducer,
    range: ChartRange? = null,
    modifier: Modifier = Modifier
) {
    ProvideVicoTheme(
        rememberM3VicoTheme()
    ) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                layers = arrayOf(
                    rememberLineCartesianLayer(
                        rangeProvider = if (range != null) CartesianLayerRangeProvider.fixed(minY = range.min, maxY = range.max) else CartesianLayerRangeProvider.auto(),
                        lineProvider = LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.rememberLine(
                                fill = LineCartesianLayer.LineFill.single(fill(MaterialTheme.colorScheme.primary)),
                                areaFill = LineCartesianLayer.AreaFill.single(fill(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))),
                            )
                        )
                    ),
                ),
                startAxis = VerticalAxis.rememberStart(
                    tick = rememberAxisTickComponent(
                        fill = Fill.Transparent
                    )
                ),
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
            modifier = modifier
        )
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