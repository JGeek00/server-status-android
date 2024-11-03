package com.jgeek00.ServerStatus.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val minimumAngle = -220f
private const val maximumAngle = 40f

@Composable
fun Gauge(
    value: String,
    percentage: Double,
    icon: @Composable () -> Unit,
    colors: List<Color>,
    size: Dp
) {
    val startAngle by remember { mutableFloatStateOf(minimumAngle) }
    val perc = percentage.coerceIn(0.0, 100.0)
    val percAngle = ((maximumAngle - minimumAngle) * perc / 100) + minimumAngle
    val animatedEndAngle by animateFloatAsState(
        targetValue = percAngle.toFloat(),
        label = "GaugeArcAnimation",
        animationSpec = tween(
            durationMillis = 300,
            easing = EaseOut
        ),
    )

    val color = getColor(percentage, colors)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(size)) {
                // Background Arc
                drawArc(
                    color = color.copy(alpha = 0.3f),
                    startAngle = minimumAngle,
                    sweepAngle = maximumAngle - minimumAngle,
                    useCenter = false,
                    style = Stroke(width = size.toPx() * 0.075f, cap = StrokeCap.Round)
                )

                // Foreground Arc
                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = animatedEndAngle - startAngle,
                    useCenter = false,
                    style = Stroke(width = size.toPx() * 0.075f, cap = StrokeCap.Round)
                )
            }

            Box(contentAlignment = Alignment.TopCenter) {
                icon()
            }
        }
        Text(
            text = value,
            fontSize = (size.value * 0.14f).sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .offset(x = 0.dp, y = -16.dp)
        )
    }
}

fun getColor(percentage: Double, colors: List<Color>): Color {
    val colorIndex = percentage / (100f / colors.size)
    return when {
        colorIndex < 1f -> colors.first()
        colorIndex >= colors.size - 1 -> colors.last()
        else -> colors[colorIndex.toInt()]
    }
}
