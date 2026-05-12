package com.ibexia.appauditor.ui.screens

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.ibexia.appauditor.ui.components.ScoreGauge
import com.ibexia.appauditor.ui.components.ScanButton
import com.ibexia.appauditor.ui.components.ThreatCard
import com.ibexia.appauditor.model.AppInfo
import com.ibexia.appauditor.viewmodel.AppViewModel
import com.ibexia.appauditor.model.RiskLevel
@Composable
fun PremiumDashboardScreen(viewModel: AppViewModel,onFixNowClick: (String) -> Unit) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    val packageManager = context.packageManager
    LaunchedEffect(Unit) {
        // Reset scan state when entering this screen
        viewModel.resetScanState()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF032B3A))
            .statusBarsPadding()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "AppAuditor Premium",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        text = "Check how secure your device is",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD0E4EA)
                    )
                }
            }

            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ScoreGauge(
                        score = uiState.deviceScore,
                        hasScanned = uiState.hasScanned
                    )
                }
            }

            item {
                ScanButton(
                    isLoading = uiState.isLoading,
                    onClick = { viewModel.scanDevice() }
                )
            }

            item {
                Text(
                    text = "All Installed Apps",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            // Display all apps, no "locked" logic
            itemsIndexed(uiState.allApps) { index, app ->    // previous used threatapp but i also create the new var with the name of allapps
                val icon = try {
                    packageManager.getApplicationIcon(app.packageName)
                } catch (e: Exception) {
                    null
                }

                AppListItem(app, icon) {
                    onFixNowClick(app.packageName)
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun AppListItem(app: AppInfo, icon: Drawable?, onClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    // Display app details
    val riskText = when (app.riskLevel) {
        RiskLevel.CRITICAL -> "Critical Risk"
        RiskLevel.HIGH -> "High Risk"
        RiskLevel.MODERATE -> "Moderate Risk"
        RiskLevel.LOW -> "Low Risk"
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { expanded = !expanded }, // Clicking on the app card navigates to app details
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A4155))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            if (icon != null) {
                Image(
                    bitmap = icon.toBitmap().asImageBitmap(),
                    contentDescription = app.appName,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f) // To push the icon to the right
            ) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                // This info is displayed in dropdown with the app details.
                // if we show this here then we have nothing to
//                Text(
//                    text = app.riskSummary,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color(0xFFD0E4EA)
//                )

                Text(
                    text = riskText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFFB74D)
                )
            }

            // Add the dropdown icon
            Icon(
                imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Dropdown",
                tint = Color.White
            )
            }


        if (expanded) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Why this app is ${app.riskLevel}:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
                // Showing detailed risk summary in the dropdown
                Text(
                    text = "The app has access to critical permission of ${app.riskSummary}",  // You can show more detailed permissions or reasons here
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFD0E4EA)
                )
            }
            Spacer(modifier = Modifier.height(16.dp)) // Add some spacing between buttons
            Button(
                onClick = { onClick() },  // This will trigger the navigation to the Revoke screen
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(50)
            ) {
                Text(text = "Fix Now", style = MaterialTheme.typography.titleMedium, color = Color.White)
            }
        }
    }

}
