package com.ibexia.appauditor.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
@Composable
fun ScoreGauge(
    score: Int,
    hasScanned: Boolean
) {
    val gaugeColor = when {
        !hasScanned -> Color.Gray
        score <= 39 -> Color.Red
        score <= 69 -> Color(0xFFFFC107)
        else -> Color(0xFF4CAF50)
    }

    val progress = if (hasScanned) score / 100f else 0f
    val displayText = if (hasScanned) "$score/100" else "?"

    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(modifier = Modifier.size(220.dp)) {

            // Background ring
            drawArc(
                color = Color(0xFF1E4D5D),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(
                    width = 26f,
                    cap = StrokeCap.Round
                )
            )

            // Colored score ring
            drawArc(
                color = gaugeColor,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(
                    width = 26f,
                    cap = StrokeCap.Round
                )
            )
        }

        Text(
            text = displayText,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )
    }
}