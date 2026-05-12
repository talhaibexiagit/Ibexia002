//package com.ibexia.appauditor.ui.screens
//
//import android.content.pm.PackageManager
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import com.ibexia.appauditor.ui.components.ScanButton
//import com.ibexia.appauditor.ui.components.ScoreGauge
//import com.ibexia.appauditor.ui.components.ThreatCard
//import com.ibexia.appauditor.viewmodel.AppViewModel
//import androidx.compose.ui.Alignment
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.wrapContentWidth
//import androidx.compose.foundation.layout.statusBarsPadding
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.foundation.background
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.statusBarsPadding
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//
//@Composable
//fun DashboardScreen(viewModel: AppViewModel) {
//    val uiState by viewModel.uiState.collectAsState()
//    val context = LocalContext.current
//    val packageManager = context.packageManager
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF032B3A))
//            .statusBarsPadding()
//    ) {
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 20.dp, vertical = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            item {
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "AppAuditor",
//                        style = MaterialTheme.typography.headlineMedium,
//                        color = Color.White
//                    )
//                    Text(
//                        text = "Check how secure your device is",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color(0xFFD0E4EA)
//                    )
//                }
//            }
//
//            item {
//                Box(
//                    modifier = Modifier.fillMaxWidth(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    ScoreGauge(
//                        score = uiState.deviceScore,
//                        hasScanned = uiState.hasScanned
//                    )
//                }
//            }
//
//            item {
//                ScanButton(
//                    isLoading = uiState.isLoading,
//                    onClick = { viewModel.scanDevice() }
//                )
//            }
//
//            item {
//                Text(
//                    text = "Top Intrusive Apps",
//                    style = MaterialTheme.typography.titleLarge,
//                    color = Color.White
//                )
//            }
//
//            if (uiState.topThreats.isEmpty()) {
//                item {
//                    Text(
//                        text = if (uiState.hasScanned) {
//                            "No major threats found."
//                        } else {
//                            "Run a scan to view the top 3 threats."
//                        },
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color(0xFFD0E4EA)
//                    )
//                }
//            } else {
//                items(uiState.topThreats) { app ->
//                    val icon = try {
//                        packageManager.getApplicationIcon(app.packageName)
//                    } catch (e: Exception) {
//                        null
//                    }
//
//                    ThreatCard(
//                        appName = app.appName,
//                        summary = app.riskSummary,
//                        riskLevel = app.riskLevel,
//                        icon = icon
//                    )
//                }
//            }
//
//            item {
//                Spacer(modifier = Modifier.height(20.dp))
//            }
//        }
//    }
//}


//this is good for sprint 4

package com.ibexia.appauditor.ui.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.ui.draw.blur
// Import necessary components
import com.ibexia.appauditor.ui.components.ScoreGauge
import com.ibexia.appauditor.ui.components.ScanButton
import com.ibexia.appauditor.ui.components.ThreatCard
import com.ibexia.appauditor.model.AppInfo
import com.ibexia.appauditor.viewmodel.AppViewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import com.ibexia.appauditor.model.RiskLevel


//
//@Composable
//fun DashboardScreen(
//    viewModel: AppViewModel,
//    onLockedAppClick: (AppInfo) -> Unit // Handle locked app click event
//) {
//    val uiState = viewModel.uiState.collectAsState().value  // Correct way to access uiState
//
//    val context = LocalContext.current
//    val packageManager = context.packageManager
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF032B3A))
//            .statusBarsPadding()
//    ) {
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(horizontal = 20.dp, vertical = 16.dp),
//            verticalArrangement = Arrangement.spacedBy(16.dp)
//        ) {
//            item {
//                Column(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        text = "AppAuditor",
//                        style = MaterialTheme.typography.headlineMedium,
//                        color = Color.White
//                    )
//                    Text(
//                        text = "Check how secure your device is",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color(0xFFD0E4EA)
//                    )
//                }
//            }
//
//            item {
//                Box(
//                    modifier = Modifier.fillMaxWidth(),
//                    contentAlignment = Alignment.Center
//                ) {
//                    ScoreGauge(
//                        score = uiState.deviceScore,
//                        hasScanned = uiState.hasScanned
//                    )
//                }
//            }
//
//            item {
//                ScanButton(
//                    isLoading = uiState.isLoading,
//                    onClick = { viewModel.scanDevice() }
//                )
//            }
//
//            item {
//                Text(
//                    text = "Top Intrusive Apps",
//                    style = MaterialTheme.typography.titleLarge,
//                    color = Color.White
//                )
//            }
//
//            // Show all apps in topThreats, at least 4 apps will be displayed
//            items(uiState.topThreats) { app ->
//                if (uiState.topThreats.indexOf(app) < 3) {
//                    // Show regular (unlocked) apps
//                    val icon = try {
//                        packageManager.getApplicationIcon(app.packageName)
//                    } catch (e: Exception) {
//                        null
//                    }
//
//                    ThreatCard(
//                        appName = app.appName,
//                        summary = app.riskSummary,
//                        riskLevel = app.riskLevel,
//                        icon = icon
//                    )
//                } else if (uiState.topThreats.indexOf(app) == 3) {
//                    // Show the 4th app as locked
//                    LockedAppCard(app = app, onClick = { onLockedAppClick(app) })
//                }
//            }
//
//            item {
//                Spacer(modifier = Modifier.height(20.dp))
//            }
//        }
//    }
//}
//
//
//@Composable
//
//fun LockedAppCard(app: AppInfo, onClick: (AppInfo) -> Unit) {
//    // Display the locked card with blurred effect and lock icon
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(120.dp)
//            .background(Color.Gray.copy(alpha = 0.5f)) // Blurred effect
//            .padding(16.dp)
//            .clickable { onClick(app) } // Navigate on click
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        )
//
//
//        {
//            Text(
//                text = app.appName,
//                style = MaterialTheme.typography.bodyMedium,
//                color = Color.White
//            )
//            Icon(
//                imageVector = Icons.Default.Lock,
//                contentDescription = "Locked App",
//                tint = Color.White
//            )
//        }
//    }
//}
//
//


@Composable
fun DashboardScreen(
    viewModel: AppViewModel,
    onLockedAppClick: (AppInfo) -> Unit // Handle locked app click event
) {
    val uiState = viewModel.uiState.collectAsState().value  // Correct way to access uiState

    // Access the context
    val context = LocalContext.current
    val packageManager = context.packageManager // Access packageManager correctly

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

            // Show apps in topThreats, at least 4 apps will be displayed
            items(uiState.topThreats) { app ->
                if (uiState.topThreats.indexOf(app) < 3) {
                    // Display regular (unlocked) apps
                    val icon = try {
                        packageManager.getApplicationIcon(app.packageName) // Correctly accessing packageManager
                    } catch (e: Exception) {
                        null
                    }

                    ThreatCard(
                        appName = app.appName,
                        summary = app.riskSummary,
                        riskLevel = app.riskLevel,
                        icon = icon
                    )
                } else if (uiState.topThreats.indexOf(app) == 3) {
                    // Show the 4th app as locked
                    LockedAppCard(app = app, onClick = { onLockedAppClick(app) })
                }
            }

            // No extra space below, it will end once the items are done
        }
    }
}


//
//@Composable
//fun LockedAppCard(app: AppInfo, onClick: (AppInfo) -> Unit) {
//    // Display the locked card with blurred effect and lock icon
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(90.dp) // Adjusted height
//            .background(Color(0xFF0A4155).copy(alpha = 0.9f)) // Set background color
//            .padding(10.dp)
//            .clickable { onClick(app) } // Click handler for navigation
//           // .clip(RoundedCornerShape(16.dp)) // Rounded corners for the locked app
//            .blur(13.dp) // Apply blur effect to the background
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Start
//        ) {
//            // Fetch and display app icon
//            val context = LocalContext.current
//            val packageManager = context.packageManager
//            val icon = try {
//                // Handle app icon fetching
//                val iconDrawable = packageManager.getApplicationIcon(app.packageName)
//                iconDrawable.toBitmap().asImageBitmap()
//            } catch (e: Exception) {
//                null
//            }
//
//            if (icon != null) {
//                Image(
//                    bitmap = icon,
//                    contentDescription = app.appName,
//                    modifier = Modifier.size(48.dp)
//                )
//            }
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            Column {
//                // Show app name
//                Text(
//                    text = app.appName,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = Color.White
//                )
//
//                // Show summary or other app details
//                Text(
//                    text = app.riskSummary,
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color(0xFFD0E4EA)
//                )
//                // Show risk level (Critical, High, Moderate, Low)
//                val riskText = when (app.riskLevel) {
//                    RiskLevel.CRITICAL -> "Critical Risk"
//                    RiskLevel.HIGH -> "High Risk"
//                    RiskLevel.MODERATE -> "Moderate Risk"
//                    RiskLevel.LOW -> "Low Risk"
//                }
//
//                Text(
//                    text = riskText,
//                    style = MaterialTheme.typography.bodySmall,
//                    color = Color(0xFFFFB74D) // Orange/yellow color for risk levels
//                )
//            }
//
//            // Show the lock icon on the right side
//            Spacer(modifier = Modifier.weight(1f)) // Push lock icon to the right
//            Icon(
//                imageVector = Icons.Default.Lock,
//                contentDescription = "Locked App",
//                tint = Color.White
//            )
//        }
//    }
//}

@Composable
fun LockedAppCard(app: AppInfo, onClick: (AppInfo) -> Unit) {
    // Display the locked card with blurred effect and lock icon
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp) // Adjusted height for the locked app card
            .clickable { onClick(app) } // Click handler for navigation
            .clip(RoundedCornerShape(16.dp)) // Apply rounded corners to the Box
            .background(Color(0xFF0A4155).copy(alpha = 0.9f)) // Set background color
            .blur(16.dp)
    ) {
        // Apply padding inside the Box and blur effect inside the content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp) // Padding around the content inside the card

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                // Fetch and display app icon
                val context = LocalContext.current
                val packageManager = context.packageManager
                val icon = try {
                    // Handle app icon fetching
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
                    // Show app name
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )

                    // Show summary or other app details
                    Text(
                        text = app.riskSummary,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD0E4EA)
                    )
                    // Show risk level (Critical, High, Moderate, Low)
                    val riskText = when (app.riskLevel) {
                        RiskLevel.CRITICAL -> "Critical Risk"
                        RiskLevel.HIGH -> "High Risk"
                        RiskLevel.MODERATE -> "Moderate Risk"
                        RiskLevel.LOW -> "Low Risk"
                    }

                    Text(
                        text = riskText,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFFB74D) // Orange/yellow color for risk levels
                    )
                }

                // Show the lock icon on the right side
                Spacer(modifier = Modifier.weight(1f)) // Push lock icon to the right
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked App",
                    tint = Color.White
                )
            }
        }
    }
}