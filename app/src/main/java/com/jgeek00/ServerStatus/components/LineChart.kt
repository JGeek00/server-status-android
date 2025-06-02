package com.jgeek00.ServerStatus.components

import androidx.compose.animation.core.snap
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.HorizontalIndicatorProperties
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.PopupProperties

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    values: List<Double>,
    color: Color,
    secondaryColor: Color,
    maxValue: Double,
    minValue: Double,
    axisFormatter: ((Double) -> String)? = null,
    tooltipFormatter: ((Int, Int, Double) -> String)? = null
) {
    LineChart(
        modifier = modifier,
        data = remember(values) {
            listOf(
                Line(
                    label = "",
                    values = values,
                    color = SolidColor(color),
                    drawStyle = DrawStyle.Stroke(width = 2.dp),
                    firstGradientFillColor = SolidColor(color).value.copy(alpha = 0.75f),
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
                            color = secondaryColor,
                            fontWeight = FontWeight.SemiBold
                        ),
                        containerColor = color,
                        contentBuilder = tooltipFormatter ?: { _, _, value -> value.toInt().toString() },
                        contentVerticalPadding = 6.dp,
                        contentHorizontalPadding = 12.dp,
                        cornerRadius = 6.dp
                    )
                )
            )
        },
        animationDelay = 0,
        maxValue = maxValue,
        minValue = minValue,
        labelHelperProperties = LabelHelperProperties(enabled = false),
        curvedEdges = false,
        indicatorProperties = HorizontalIndicatorProperties(
            enabled = true,
            contentBuilder = axisFormatter ?: { it.toInt().toString() },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.End
            )
        )
    )
}