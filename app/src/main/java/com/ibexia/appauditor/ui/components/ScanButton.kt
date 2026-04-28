package com.ibexia.appauditor.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color

@Composable
fun ScanButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(700),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Button(
        onClick = onClick,
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF4D5A),
            contentColor = Color.White,
            disabledContainerColor = Color(0x66FF4D5A),
            disabledContentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .scale(if (isLoading) 1f else scale)
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Scan Device Now",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}