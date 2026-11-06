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

    // Group-level permission consolidation: Android Settings groups runtime permissions
    // into single toggles (Location, SMS, Storage, Contacts, Phone). Display only one entry per group to prevent redundancy.
    // Sorted by priority: CRITICAL permissions at top, HIGH risk permissions below.
    val phoneGroup = listOf(
        "android.permission.READ_PHONE_STATE",
        "android.permission.READ_CALL_LOG",
        "android.permission.CALL_PHONE"
    )
    val storageGroup = listOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE",
        "android.permission.READ_MEDIA_IMAGES",
        "android.permission.READ_MEDIA_VIDEO",
        "android.permission.READ_MEDIA_AUDIO"
    )
    val criticalPermissions = appInfo.permissions.filter {
        // Filter critical/high permissions
        viewModel.permissionRiskMap[it] == RiskLevel.CRITICAL || viewModel.permissionRiskMap[it] == RiskLevel.HIGH
    }.filterNot { permission ->
        // If app has ACCESS_FINE_LOCATION, omit ACCESS_COARSE_LOCATION (both controlled by the same Location toggle)
        (permission == "android.permission.ACCESS_COARSE_LOCATION" && "android.permission.ACCESS_FINE_LOCATION" in appInfo.permissions) ||
        // If app has READ_SMS, omit SEND_SMS (both controlled by the same SMS toggle)
        (permission == "android.permission.SEND_SMS" && "android.permission.READ_SMS" in appInfo.permissions) ||
        // Consolidate Storage permissions to a single entry matching "Storage / Photos & videos" toggle in Settings
        (permission in storageGroup && permission != storageGroup.firstOrNull { it in appInfo.permissions }) ||
        // If app has READ_CONTACTS, omit WRITE_CONTACTS (both controlled by the same Contacts and accounts toggle)
        (permission == "android.permission.WRITE_CONTACTS" && "android.permission.READ_CONTACTS" in appInfo.permissions) ||
        // If app has READ_CALENDAR, omit WRITE_CALENDAR (both controlled by the same Calendar toggle)
        (permission == "android.permission.WRITE_CALENDAR" && "android.permission.READ_CALENDAR" in appInfo.permissions) ||
        // Consolidate Phone / Calls permissions to a single entry matching "Phone" toggle in Settings
        (permission in phoneGroup && permission != phoneGroup.firstOrNull { it in appInfo.permissions }) ||
        // GET_ACCOUNTS is a normal install-time permission on Android 8.0+ (API 26+) and cannot be revoked in Android Settings
        permission == "android.permission.GET_ACCOUNTS" ||
        // SYSTEM_ALERT_WINDOW is managed under "Special app access / Appear on top", not standard App Permissions
        permission == "android.permission.SYSTEM_ALERT_WINDOW"
    }.sortedByDescending { viewModel.permissionRiskMap[it]?.ordinal ?: 0 }

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

            if (criticalPermissions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A4155))
                    ) {
                        Text(
                            text = "No high-risk permissions found. All permissions are safe.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFD0E4EA),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                // Show only critical/high permissions
                items(criticalPermissions) { permission ->
                    PermissionItem(permission = permission, onRevokeClick = {
                        // Revoke permission when button is clicked
                        viewModel.revokePermission(packageName)
                    })
                }
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
        // CRITICAL PERMISSIONS (Camera, Mic, SMS, Contacts, Storage)
        "android.permission.CAMERA" -> RiskLevel.CRITICAL
        "android.permission.RECORD_AUDIO" -> RiskLevel.CRITICAL
        "android.permission.READ_SMS" -> RiskLevel.CRITICAL
        "android.permission.SEND_SMS" -> RiskLevel.CRITICAL
        "android.permission.READ_CONTACTS" -> RiskLevel.CRITICAL
        "android.permission.WRITE_CONTACTS" -> RiskLevel.CRITICAL
        "android.permission.READ_EXTERNAL_STORAGE" -> RiskLevel.CRITICAL
        "android.permission.WRITE_EXTERNAL_STORAGE" -> RiskLevel.CRITICAL
        "android.permission.READ_MEDIA_IMAGES" -> RiskLevel.CRITICAL
        "android.permission.READ_MEDIA_VIDEO" -> RiskLevel.CRITICAL
        "android.permission.READ_MEDIA_AUDIO" -> RiskLevel.CRITICAL

        // HIGH RISK PERMISSIONS (Calendar, Phone/Calls, Location)
        "android.permission.ACCESS_FINE_LOCATION" -> RiskLevel.HIGH
        "android.permission.ACCESS_COARSE_LOCATION" -> RiskLevel.HIGH
        "android.permission.READ_CALENDAR" -> RiskLevel.HIGH
        "android.permission.WRITE_CALENDAR" -> RiskLevel.HIGH
        "android.permission.READ_PHONE_STATE" -> RiskLevel.HIGH
        "android.permission.READ_CALL_LOG" -> RiskLevel.HIGH
        "android.permission.CALL_PHONE" -> RiskLevel.HIGH
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
                    color = when (permissionRiskLevel) {
                        RiskLevel.CRITICAL -> Color(0xFFFF4D5A) // Red for Critical
                        RiskLevel.HIGH -> Color(0xFFFFB74D) // Yellow/Orange for High
                        else -> Color(0xFF4CAF50)
                    }
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
        // Location group (Single toggle in Android Settings)
        "android.permission.ACCESS_FINE_LOCATION" -> "Location Access"
        "android.permission.ACCESS_COARSE_LOCATION" -> "Location Access"
        // SMS group (Single toggle in Android Settings)
        "android.permission.READ_SMS" -> "SMS Access"
        "android.permission.SEND_SMS" -> "SMS Access"
        // Phone group (Matches "Phone" toggle in Android Settings)
        "android.permission.READ_PHONE_STATE" -> "Phone Access"
        "android.permission.READ_CALL_LOG" -> "Phone Access"
        "android.permission.CALL_PHONE" -> "Phone Access"
        // Contacts & Accounts group (Matches "Contacts and accounts" toggle in Android Settings)
        "android.permission.READ_CONTACTS" -> "Contacts and accounts"
        "android.permission.WRITE_CONTACTS" -> "Contacts and accounts"
        // Original: "android.permission.GET_ACCOUNTS" -> "Account Access"
        "android.permission.GET_ACCOUNTS" -> "Contacts and accounts"
        // Storage & Media group (Universal: Matches "Photos and videos" in Android 13+ and "Storage" in older Android)
        "android.permission.READ_EXTERNAL_STORAGE" -> "Photos, videos & storage"
        "android.permission.WRITE_EXTERNAL_STORAGE" -> "Photos, videos & storage"
        "android.permission.READ_MEDIA_IMAGES" -> "Photos, videos & storage"
        "android.permission.READ_MEDIA_VIDEO" -> "Photos, videos & storage"
        "android.permission.READ_MEDIA_AUDIO" -> "Photos, videos & storage"
        "android.permission.READ_CALENDAR" -> "Calendar Access"
        "android.permission.WRITE_CALENDAR" -> "Calendar Access"
        // Original: "android.permission.SYSTEM_ALERT_WINDOW" -> "System Alert Window Access"
        // Commented out: SYSTEM_ALERT_WINDOW is managed under "Special app access / Appear on top", not standard App Permissions.
        else -> permission.replace("android.permission.", "").replace("_", " ")
    }
}
