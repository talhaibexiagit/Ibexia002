package com.ibexia.appauditor.ui.screens

import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.ibexia.appauditor.R
import com.ibexia.appauditor.ads.AdMobManager
import com.ibexia.appauditor.ads.BannerAdView
import com.ibexia.appauditor.model.AppInfo
import com.ibexia.appauditor.model.RiskLevel
import com.ibexia.appauditor.ui.components.ExpandableThreatCard
import com.ibexia.appauditor.ui.components.LowScoreAlert
import com.ibexia.appauditor.ui.components.ScanButton
import com.ibexia.appauditor.ui.components.ScoreGauge
import com.ibexia.appauditor.viewmodel.AppViewModel

@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    onLockedAppClick: (AppInfo) -> Unit,
    onFixNowClick: (String) -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsState().value
    val context = LocalContext.current
    val packageManager = context.packageManager

    // Task 2: Freemium "Fix Now" Ad Wall dialog state
    var showAdDialog by remember { mutableStateOf(false) }
    var pendingFixApp by remember { mutableStateOf<AppInfo?>(null) }

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

            // Score Gauge Section + Task 1: Low Score Warning (< 25)
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ScoreGauge(
                        score = uiState.deviceScore,
                        hasScanned = uiState.hasScanned
                    )

                    // Task 1: Low Score Warning under Score Gauge (< 25)
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
                    onClick = {
                        val activity = context as? Activity
                        if (activity != null) {
                            AdMobManager.showInterstitial(activity) {
                                viewModel.scanDevice()
                            }
                        } else {
                            viewModel.scanDevice()
                        }
                    }
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

            // Threats List or Unscanned State
            if (!uiState.hasScanned) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    BannerAdView()
                }
            } else {
                item {
                    Text(
                        text = "Top Intrusive Apps",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }

                if (uiState.topThreats.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0A4155))
                        ) {
                            Text(
                                text = "No major threats found.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFD0E4EA),
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    // Task 2: Display threats with expandability and Fix Now handling
                    itemsIndexed(uiState.topThreats) { index, app ->
                        val icon = try {
                            packageManager.getApplicationIcon(app.packageName)
                        } catch (e: Exception) {
                            null
                        }

                        // Freemium users can expand/click only the top 3 displayed apps; Premium users can expand all
                        val isExpandable = if (uiState.isPremium) true else index < 3

                        ExpandableThreatCard(
                            app = app,
                            icon = icon,
                            isExpandable = isExpandable,
                            onFixNowClick = {
                                if (uiState.isPremium) {
                                    // Premium users bypass ad requirement
                                    onFixNowClick(app.packageName)
                                } else {
                                    // Freemium users require ad wall
                                    pendingFixApp = app
                                    showAdDialog = true
                                }
                            }
                        )
                    }

                    // For freemium users, show locked card if additional threats exist on device
                    if (uiState.isFreemium) {
                        val lockedApp = uiState.allApps.firstOrNull {
                            (it.riskLevel == RiskLevel.CRITICAL || it.riskLevel == RiskLevel.HIGH) &&
                                    uiState.topThreats.none { threat -> threat.packageName == it.packageName }
                        }
                        if (lockedApp != null) {
                            item {
                                LockedAppCard(
                                    app = lockedApp,
                                    onClick = { onLockedAppClick(lockedApp) }
                                )
                            }
                        }
                    }
                }

                // Scanned state banner ad below threats
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    BannerAdView()
                }
            }
        }

        // Task 2: Freemium "Fix Now" Ad Wall Dialog
        if (showAdDialog && pendingFixApp != null) {
            AlertDialog(
                onDismissRequest = {
                    showAdDialog = false
                    pendingFixApp = null
                },
                containerColor = Color(0xFF0A4155),
                title = {
                    Text(
                        text = "Unlock Premium Fix",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        text = "Watch a short ad to fix this threat.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD0E4EA)
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val appToFix = pendingFixApp
                            showAdDialog = false
                            pendingFixApp = null
                            val activity = context as? Activity
                            if (activity != null && appToFix != null) {
                                AdMobManager.showRewarded(
                                    activity = activity,
                                    onRewardEarned = {
                                        onFixNowClick(appToFix.packageName)
                                    }
                                )
                            } else if (appToFix != null) {
                                onFixNowClick(appToFix.packageName)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "Watch Ad",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showAdDialog = false
                            pendingFixApp = null
                        }
                    ) {
                        Text(
                            text = "Cancel",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun LockedAppCard(app: AppInfo, onClick: (AppInfo) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onClick(app) }
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0A4155).copy(alpha = 0.9f))
            .blur(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                val context = LocalContext.current
                val packageManager = context.packageManager
                val icon = try {
                    val iconDrawable = packageManager.getApplicationIcon(app.packageName)
                    iconDrawable.toBitmap().asImageBitmap()
                } catch (e: Exception) {
                    null
                }

                if (icon != null) {
                    Image(
                        bitmap = icon,
                        contentDescription = app.appName,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    Text(
                        text = app.riskSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD0E4EA)
                    )

                    val riskText = when (app.riskLevel) {
                        RiskLevel.CRITICAL -> "Critical Risk"
                        RiskLevel.HIGH -> "High Risk"
                        RiskLevel.MODERATE -> "Moderate Risk"
                        RiskLevel.LOW -> "Low Risk"
                    }

                    Text(
                        text = riskText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFFB74D)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked App",
                    tint = Color.White
                )
            }
        }
    }
}
