package com.ibexia.appauditor.ui.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ibexia.appauditor.ui.components.ScanButton
import com.ibexia.appauditor.ui.components.ScoreGauge
import com.ibexia.appauditor.ui.components.ThreatCard
import com.ibexia.appauditor.viewmodel.AppViewModel
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*

@Composable
fun DashboardScreen(viewModel: AppViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val packageManager = context.packageManager

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
                        text = "AppAuditor",
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
                    text = "Top Intrusive Apps",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            if (uiState.topThreats.isEmpty()) {
                item {
                    Text(
                        text = if (uiState.hasScanned) {
                            "No major threats found."
                        } else {
                            "Run a scan to view the top 3 threats."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD0E4EA)
                    )
                }
            } else {
                items(uiState.topThreats) { app ->
                    val icon = try {
                        packageManager.getApplicationIcon(app.packageName)
                    } catch (e: Exception) {
                        null
                    }

                    ThreatCard(
                        appName = app.appName,
                        summary = app.riskSummary,
                        riskLevel = app.riskLevel,
                        icon = icon
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}