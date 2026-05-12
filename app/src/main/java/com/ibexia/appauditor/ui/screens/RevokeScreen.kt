package com.ibexia.appauditor.ui.screens

import android.content.pm.PackageManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import com.ibexia.appauditor.viewmodel.AppViewModel
import com.ibexia.appauditor.model.AppInfo
import com.ibexia.appauditor.model.RiskLevel
@Composable
fun RevokeScreen(packageName: String, viewModel: AppViewModel) {
    val uiState = viewModel.uiState.collectAsState().value
    val appInfo = uiState.allApps.firstOrNull { it.packageName == packageName }

    if (appInfo == null) {
        // Handle error if the app is not found
        return
    }

    val criticalPermissions = appInfo.permissions.filter {
        // Filter critical/high permissions
        viewModel.permissionRiskMap[it] == RiskLevel.CRITICAL || viewModel.permissionRiskMap[it] == RiskLevel.HIGH
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
                        text = "App Details",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "App: ${appInfo.appName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFD0E4EA)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Risk Level: ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = appInfo.riskLevel.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = when (appInfo.riskLevel) {
                                RiskLevel.CRITICAL -> Color(0xFFFF4D5A) // Red for Critical
                                RiskLevel.HIGH -> Color(0xFFFFB74D) // Yellow for High
                                RiskLevel.MODERATE -> Color(0xFF4CAF50) // Green for Moderate
                                RiskLevel.LOW -> Color(0xFF4CAF50) // Green for Low
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Text(
                    text = "High Priority Permissions",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
            }

            // Show only critical/high permissions
            items(criticalPermissions) { permission ->
                PermissionItem(permission = permission, onRevokeClick = {
                    // Revoke permission when button is clicked
                    viewModel.revokePermission(packageName)
                })
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun PermissionItem(permission: String, onRevokeClick: () -> Unit) {
    // Simplified permission display name
    val permissionName = getPermissionDisplayName(permission)

    val permissionRiskLevel = when (permission) {
        "android.permission.CAMERA" -> RiskLevel.CRITICAL
        "android.permission.RECORD_AUDIO" -> RiskLevel.CRITICAL
        "android.permission.ACCESS_FINE_LOCATION" -> RiskLevel.CRITICAL
        "android.permission.READ_SMS" -> RiskLevel.CRITICAL
        "android.permission.SEND_SMS" -> RiskLevel.CRITICAL
        "android.permission.READ_CALL_LOG" -> RiskLevel.CRITICAL
        "android.permission.READ_CONTACTS" -> RiskLevel.CRITICAL
        "android.permission.ACCESS_COARSE_LOCATION" -> RiskLevel.HIGH
        "android.permission.READ_EXTERNAL_STORAGE" -> RiskLevel.HIGH
        "android.permission.WRITE_EXTERNAL_STORAGE" -> RiskLevel.HIGH
        "android.permission.GET_ACCOUNTS" -> RiskLevel.HIGH
        "android.permission.READ_CALENDAR" -> RiskLevel.HIGH
        "android.permission.SYSTEM_ALERT_WINDOW" -> RiskLevel.HIGH
        else -> RiskLevel.LOW
    }

    val riskText = when (permissionRiskLevel) {
        RiskLevel.CRITICAL -> "Critical Risk"
        RiskLevel.HIGH -> "High Risk"
        else -> "Low Risk"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
         //   .clickable { /* Handle click if needed */ },
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = permissionName,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
                Text(
                    text = riskText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFFFB74D)
                )
            }

            // Revoke Button
            Button(
                onClick = onRevokeClick,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                shape = RoundedCornerShape(20)
            ) {
                Text(
                    text = "Revoke",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
        }
    }
}

fun getPermissionDisplayName(permission: String): String {
    return when (permission) {
        "android.permission.CAMERA" -> "Camera Access"
        "android.permission.RECORD_AUDIO" -> "Microphone Access"
        "android.permission.ACCESS_FINE_LOCATION" -> "Location Access"
        "android.permission.READ_SMS" -> "SMS Access"
        "android.permission.SEND_SMS" -> "Send SMS"
        "android.permission.READ_CALL_LOG" -> "Call Log Access"
        "android.permission.READ_CONTACTS" -> "Contacts Access"
        "android.permission.ACCESS_COARSE_LOCATION" -> "Coarse Location Access"
        "android.permission.READ_EXTERNAL_STORAGE" -> "External Storage Access"
        "android.permission.WRITE_EXTERNAL_STORAGE" -> "External Storage Write Access"
        "android.permission.GET_ACCOUNTS" -> "Account Access"
        "android.permission.READ_CALENDAR" -> "Calendar Access"
        "android.permission.SYSTEM_ALERT_WINDOW" -> "System Alert Window Access"
        else -> permission.replace("android.permission.", "").replace("_", " ")
    }
}