package com.ibexia.appauditor.ui.screens

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
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
import com.ibexia.appauditor.ui.components.LowScoreAlert
import com.ibexia.appauditor.ui.components.ScanButton
import com.ibexia.appauditor.ui.components.ThreatCard
import com.ibexia.appauditor.model.AppInfo
import com.ibexia.appauditor.viewmodel.AppViewModel
import com.ibexia.appauditor.model.RiskLevel
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.ibexia.appauditor.R

@Composable
fun PremiumDashboardScreen(viewModel: AppViewModel, onFixNowClick: (String) -> Unit) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    val packageManager = context.packageManager

    // Rescan notification feedback
    var hasScannedOnce by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.hasScanned, uiState.isLoading) {
        if (uiState.hasScanned && !uiState.isLoading && hasScannedOnce) {
            Toast.makeText(context, "Scan complete: Results updated!", Toast.LENGTH_SHORT).show()
        }
        if (uiState.hasScanned && !uiState.isLoading) {
            hasScannedOnce = true
        }
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
            // Header with Privasei branding
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.privasei_icon),
                            contentDescription = "Privasei Logo",
                            modifier = Modifier.size(38.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Row {
                            Text(
                                text = "PRIVA",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color.White
                            )
                            Text(
                                text = "SEI",
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                color = Color(0xFFA855F7)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Check how secure your device is",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD0E4EA)
                    )
                }
            }

            // Score Gauge Section + Low Score Warning (< 25)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScoreGauge(
                        score = uiState.deviceScore,
                        hasScanned = uiState.hasScanned
                    )
                    if (uiState.hasScanned && uiState.deviceScore < 25) {
                        Spacer(modifier = Modifier.height(14.dp))
                        LowScoreAlert()
                    }
                }
            }

            // Scan / Rescan Device Button
            item {
                ScanButton(
                    isLoading = uiState.isLoading,
                    hasScanned = uiState.hasScanned,
                    onClick = { viewModel.scanDevice() }
                )

                // Rescan informational hint
                if (uiState.hasScanned) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0A4155).copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = Color(0xFFFFB74D),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Fixed threats? Tap 'Rescan Now' to update results.",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = Color(0xFFD0E4EA)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "All Installed Apps",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            // Strictly sorted: Critical first, then High, then Moderate, then Low
            val sortedApps = uiState.allApps.sortedWith(
                compareByDescending<AppInfo> { it.riskLevel.ordinal }
                    .thenBy { it.appName.lowercase() }
            )

            // Display all apps strictly sorted by risk level
            itemsIndexed(sortedApps) { index, app ->
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
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A4155))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    Text(
                        text = riskText,
                        style = MaterialTheme.typography.bodySmall,
                        color = when (app.riskLevel) {
                            RiskLevel.CRITICAL -> Color(0xFFFF4D5A)
                            RiskLevel.HIGH -> Color(0xFFFFB74D)
                            else -> Color(0xFF4CAF50)
                        }
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Dropdown",
                    tint = Color.White
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                if (app.riskLevel == RiskLevel.CRITICAL || app.riskLevel == RiskLevel.HIGH) {
                    Column {
                        Text(
                            text = "Why this app is ${app.riskLevel}:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Text(
                            text = "The app has access to critical permission of ${app.riskSummary}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD0E4EA)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onClick() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "Fix Now",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )
                    }
                } else {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "This app only uses basic system features (Bluetooth, Wi-Fi, etc.). No action required.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD0E4EA)
                            )
                        }
                    }
                }
            }
        }
    }
}
