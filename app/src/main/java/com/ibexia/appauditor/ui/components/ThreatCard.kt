package com.ibexia.appauditor.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.ibexia.appauditor.model.RiskLevel
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.graphics.Color

@Composable
fun ThreatCard(
    appName: String,
    summary: String,
    riskLevel: RiskLevel,
    icon: Drawable?
) {
    val riskText = when (riskLevel) {
        RiskLevel.CRITICAL -> "Critical Risk"
        RiskLevel.HIGH -> "High Risk"
        RiskLevel.MODERATE -> "Moderate Risk"
        RiskLevel.LOW -> "Low Risk"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0A4155)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            if (icon != null) {
                Image(
                    bitmap = icon.toBitmap().asImageBitmap(),
                    contentDescription = appName,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )

                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFD0E4EA)
                )

                Text(
                    text = riskText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFFB74D)
                )
            }
        }
    }
}