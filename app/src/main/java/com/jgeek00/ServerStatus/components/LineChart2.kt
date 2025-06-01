package com.jgeek00.ServerStatus.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.LabelHelperProperties
import ir.ehsannarmani.compose_charts.models.Line

@Composable
fun LineChart2(
    modifier: Modifier = Modifier,
    values: List<Double>,
    color: Color,
    maxValue: Double,
    minValue: Double
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
                )
            )
        },
        animationDelay = 0,
        maxValue = maxValue,
        minValue = minValue,
        labelHelperProperties = LabelHelperProperties(enabled = false)
    )
}