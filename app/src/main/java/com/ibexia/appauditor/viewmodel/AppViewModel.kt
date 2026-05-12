//package com.ibexia.appauditor.viewmodel
//
//import android.app.Application
//import android.content.pm.ApplicationInfo as AndroidAppInfo
//import android.content.pm.PackageManager
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.viewModelScope
//import com.ibexia.appauditor.model.AppInfo
//import com.ibexia.appauditor.model.RiskLevel
//import com.ibexia.appauditor.model.ScanUiState
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class AppViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val packageManager: PackageManager =
//        getApplication<Application>().packageManager
//
//    private val _uiState = MutableStateFlow(ScanUiState())
//    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()
//
//    private val permissionRiskMap = mapOf(
//        // CRITICAL
//        "android.permission.RECORD_AUDIO" to RiskLevel.CRITICAL,
//        "android.permission.CAMERA" to RiskLevel.CRITICAL,
//        "android.permission.ACCESS_FINE_LOCATION" to RiskLevel.CRITICAL,
//        "android.permission.READ_SMS" to RiskLevel.CRITICAL,
//        "android.permission.SEND_SMS" to RiskLevel.CRITICAL,
//        "android.permission.READ_CALL_LOG" to RiskLevel.CRITICAL,
//        "android.permission.READ_CONTACTS" to RiskLevel.CRITICAL,
//
//        // HIGH
//        "android.permission.ACCESS_COARSE_LOCATION" to RiskLevel.HIGH,
//        "android.permission.READ_EXTERNAL_STORAGE" to RiskLevel.HIGH,
//        "android.permission.WRITE_EXTERNAL_STORAGE" to RiskLevel.HIGH,
//        "android.permission.GET_ACCOUNTS" to RiskLevel.HIGH,
//        "android.permission.READ_CALENDAR" to RiskLevel.HIGH,
//        "android.permission.SYSTEM_ALERT_WINDOW" to RiskLevel.HIGH,
//
//        // MODERATE
//        "android.permission.ACCESS_NETWORK_STATE" to RiskLevel.MODERATE,
//        "android.permission.ACCESS_WIFI_STATE" to RiskLevel.MODERATE,
//        "android.permission.BLUETOOTH" to RiskLevel.MODERATE,
//        "android.permission.READ_PHONE_STATE" to RiskLevel.MODERATE,
//
//        // LOW
//        "android.permission.INTERNET" to RiskLevel.LOW,
//        "android.permission.VIBRATE" to RiskLevel.LOW,
//        "android.permission.WAKE_LOCK" to RiskLevel.LOW,
//        "android.permission.RECEIVE_BOOT_COMPLETED" to RiskLevel.LOW
//    )
//    // This is just for screen 4 when we come from screen 2 to screen 4 so reset all the states.
//    fun resetScanState() {
//        _uiState.value = ScanUiState(
//            isLoading = false,
//            hasScanned = false,
//            deviceScore = 0,
//            overallRisk = RiskLevel.LOW,
//            topThreats = emptyList() // Reset topThreats to empty list
//        )
//    }
//    fun scanDevice() {
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(isLoading = true)
//
//            delay(2000) // UI loading state ke liye
//
//            val result = analyzeApps()
//
//            _uiState.value = ScanUiState(
//                isLoading = false,
//                hasScanned = true,
//                deviceScore = result.first,
//                overallRisk = getOverallRiskFromScore(result.first),
//                topThreats = result.second.sortedByDescending { it.riskLevel.ordinal }, // .take(3) this is used in sprint 3 just for 3 top apps
//                allApps = result.second.sortedByDescending { it.riskLevel.ordinal }
//            )
//        }
//    }
//
//    private fun analyzeApps(): Pair<Int, List<AppInfo>> {
//        val allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
//
//        var finalScore = 100f
//        val analyzedApps = mutableListOf<AppInfo>()
//
//        for (app in allApps) {
//            val packageName = app.packageName
//            val appName = packageManager.getApplicationLabel(app).toString()
//
//            val isUserApp = (app.flags and AndroidAppInfo.FLAG_SYSTEM) == 0
//            val isNotGoogleOrAndroid =
//                !packageName.startsWith("com.android") &&
//                        !packageName.startsWith("com.google")
//            val isNotSelf = packageName != getApplication<Application>().packageName
//
//            if (isUserApp && isNotGoogleOrAndroid && isNotSelf) {
//                val permissions = getAppPermissions(packageName).toSet().toList()
//                val riskLevel = getHighestRisk(permissions.toSet())
//                val riskSummary = getRiskSummary(permissions, riskLevel)
//
//                val appDeduction = when (riskLevel) {
//                    RiskLevel.CRITICAL -> 2f
//                    RiskLevel.HIGH -> 1f
//                    RiskLevel.MODERATE -> 0f
//                    RiskLevel.LOW -> 0f
//                }
//
//                finalScore -= appDeduction
//
//                analyzedApps.add(
//                    AppInfo(
//                        appName = appName,
//                        packageName = packageName,
//                        permissions = permissions,
//                        riskLevel = riskLevel,
//                        riskSummary = riskSummary
//                    )
//                )
//            }
//        }
//
//        finalScore = maxOf(0f, finalScore)
//        return finalScore.toInt() to analyzedApps
//    }
//
//    private fun getHighestRisk(permissions: Set<String>): RiskLevel {
//        var highestRisk = RiskLevel.LOW
//
//        for (permission in permissions) {
//            val risk = permissionRiskMap[permission] ?: continue
//            if (risk.ordinal > highestRisk.ordinal) {
//                highestRisk = risk
//            }
//        }
//
//        return highestRisk
//    }
//
//    private fun getAppPermissions(packageName: String): List<String> {
//        return try {
//            val packageInfo = packageManager.getPackageInfo(
//                packageName,
//                PackageManager.GET_PERMISSIONS
//            )
//            packageInfo.requestedPermissions?.toList() ?: emptyList()
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }
//
//    private fun getRiskSummary(
//        permissions: List<String>,
//        riskLevel: RiskLevel
//    ): String {
//        val dangerousPermission = permissions.firstOrNull { permissionRiskMap[it] == riskLevel }
//
//        return when (dangerousPermission) {
//            "android.permission.RECORD_AUDIO" -> "Accesses microphone"
//            "android.permission.CAMERA" -> "Uses camera access"
//            "android.permission.ACCESS_FINE_LOCATION" -> "Tracks precise location"
//            "android.permission.READ_SMS" -> "Reads SMS messages"
//            "android.permission.SEND_SMS" -> "Can send SMS"
//            "android.permission.READ_CALL_LOG" -> "Reads call history"
//            "android.permission.READ_CONTACTS" -> "Reads contacts"
//            "android.permission.ACCESS_COARSE_LOCATION" -> "Uses location access"
//            "android.permission.SYSTEM_ALERT_WINDOW" -> "Can draw over other apps"
//            "android.permission.READ_EXTERNAL_STORAGE" -> "Reads device storage"
//            "android.permission.WRITE_EXTERNAL_STORAGE" -> "Writes to device storage"
//            else -> "Has sensitive permissions"
//        }
//    }
//
//    private fun getOverallRiskFromScore(score: Int): RiskLevel {
//        return when {
//            score <= 39 -> RiskLevel.CRITICAL
//            score <= 69 -> RiskLevel.HIGH
//            score <= 84 -> RiskLevel.MODERATE
//            else -> RiskLevel.LOW
//        }
//    }
//}

package com.ibexia.appauditor.viewmodel

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo as AndroidAppInfo
import android.content.pm.PackageManager
import android.net.Uri
//import androidx.compose.ui.text.font.FontVariation.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ibexia.appauditor.model.AppInfo
import com.ibexia.appauditor.model.RiskLevel
import com.ibexia.appauditor.model.ScanUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.provider.Settings

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val packageManager: PackageManager =
        getApplication<Application>().packageManager

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    val permissionRiskMap = mapOf(
        // CRITICAL
        "android.permission.RECORD_AUDIO" to RiskLevel.CRITICAL,
        "android.permission.CAMERA" to RiskLevel.CRITICAL,
        "android.permission.ACCESS_FINE_LOCATION" to RiskLevel.CRITICAL,
        "android.permission.READ_SMS" to RiskLevel.CRITICAL,
        "android.permission.SEND_SMS" to RiskLevel.CRITICAL,
        "android.permission.READ_CALL_LOG" to RiskLevel.CRITICAL,
        "android.permission.READ_CONTACTS" to RiskLevel.CRITICAL,

        // HIGH
        "android.permission.ACCESS_COARSE_LOCATION" to RiskLevel.HIGH,
        "android.permission.READ_EXTERNAL_STORAGE" to RiskLevel.HIGH,
        "android.permission.WRITE_EXTERNAL_STORAGE" to RiskLevel.HIGH,
        "android.permission.GET_ACCOUNTS" to RiskLevel.HIGH,
        "android.permission.READ_CALENDAR" to RiskLevel.HIGH,
        "android.permission.SYSTEM_ALERT_WINDOW" to RiskLevel.HIGH,

        // MODERATE
        "android.permission.ACCESS_NETWORK_STATE" to RiskLevel.MODERATE,
        "android.permission.ACCESS_WIFI_STATE" to RiskLevel.MODERATE,
        "android.permission.BLUETOOTH" to RiskLevel.MODERATE,
        "android.permission.READ_PHONE_STATE" to RiskLevel.MODERATE,

        // LOW
        "android.permission.INTERNET" to RiskLevel.LOW,
        "android.permission.VIBRATE" to RiskLevel.LOW,
        "android.permission.WAKE_LOCK" to RiskLevel.LOW,
        "android.permission.RECEIVE_BOOT_COMPLETED" to RiskLevel.LOW
    )

    fun resetScanState() {
        _uiState.value = ScanUiState(
            isLoading = false,
            hasScanned = false,
            deviceScore = 0,
            overallRisk = RiskLevel.LOW,
            topThreats = emptyList(),
            allApps = emptyList()
        )
    }

    fun scanDevice() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            delay(2000) // Simulate scanning delay
            val result = analyzeApps()

            _uiState.value = ScanUiState(
                isLoading = false,
                hasScanned = true,
                deviceScore = result.first,
                overallRisk = getOverallRiskFromScore(result.first),
                topThreats = result.second.sortedByDescending { it.riskLevel.ordinal },
                allApps = result.second.sortedByDescending { it.riskLevel.ordinal }
            )
        }
    }

    private fun analyzeApps(): Pair<Int, List<AppInfo>> {
        val allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        var finalScore = 100f
        val analyzedApps = mutableListOf<AppInfo>()

        for (app in allApps) {
            val packageName = app.packageName
            val appName = packageManager.getApplicationLabel(app).toString()

            val isUserApp = (app.flags and AndroidAppInfo.FLAG_SYSTEM) == 0
            val isNotGoogleOrAndroid =
                !packageName.startsWith("com.android") && !packageName.startsWith("com.google")
            val isNotSelf = packageName != getApplication<Application>().packageName

            if (isUserApp && isNotGoogleOrAndroid && isNotSelf) {
                val permissions = getAppPermissions(packageName).toSet().toList()
                val riskLevel = getHighestRisk(permissions.toSet())
                val riskSummary = getRiskSummary(permissions, riskLevel)

                val appDeduction = when (riskLevel) {
                    RiskLevel.CRITICAL -> 2f
                    RiskLevel.HIGH -> 1f
                    RiskLevel.MODERATE -> 0f
                    RiskLevel.LOW -> 0f
                }

                finalScore -= appDeduction

                analyzedApps.add(
                    AppInfo(
                        appName = appName,
                        packageName = packageName,
                        permissions = permissions,
                        riskLevel = riskLevel,
                        riskSummary = riskSummary
                    )
                )
            }
        }

        finalScore = maxOf(0f, finalScore)
        return finalScore.toInt() to analyzedApps
    }

    fun getHighestRisk(permissions: Set<String>): RiskLevel {
        var highestRisk = RiskLevel.LOW

        for (permission in permissions) {
            val risk = permissionRiskMap[permission] ?: continue
            if (risk.ordinal > highestRisk.ordinal) {
                highestRisk = risk
            }
        }

        return highestRisk
    }

    fun getAppPermissions(packageName: String): List<String> {
        val activePermissions = mutableListOf<String>()

        try {
            val packageInfo = packageManager.getPackageInfo(
                packageName, PackageManager.GET_PERMISSIONS
            )

            packageInfo.requestedPermissions?.forEach { permission ->
                val permissionStatus = packageManager.checkPermission(permission, packageName)
                if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                    activePermissions.add(permission)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return activePermissions
    }

    fun getRiskSummary(
        permissions: List<String>,
        riskLevel: RiskLevel
    ): String {
        val dangerousPermission = permissions.firstOrNull { permissionRiskMap[it] == riskLevel }

        return when (dangerousPermission) {
            "android.permission.RECORD_AUDIO" -> "Accesses microphone"
            "android.permission.CAMERA" -> "Uses camera access"
            "android.permission.ACCESS_FINE_LOCATION" -> "Tracks precise location"
            "android.permission.READ_SMS" -> "Reads SMS messages"
            "android.permission.SEND_SMS" -> "Can send SMS"
            "android.permission.READ_CALL_LOG" -> "Reads call history"
            "android.permission.READ_CONTACTS" -> "Reads contacts"
            "android.permission.ACCESS_COARSE_LOCATION" -> "Uses location access"
            "android.permission.SYSTEM_ALERT_WINDOW" -> "Can draw over other apps"
            "android.permission.READ_EXTERNAL_STORAGE" -> "Reads device storage"
            "android.permission.WRITE_EXTERNAL_STORAGE" -> "Writes to device storage"
            else -> "Has sensitive permissions"
        }
    }

    private fun getOverallRiskFromScore(score: Int): RiskLevel {
        return when {
            score <= 39 -> RiskLevel.CRITICAL
            score <= 69 -> RiskLevel.HIGH
            score <= 84 -> RiskLevel.MODERATE
            else -> RiskLevel.LOW
        }
    }

    fun revokePermission(packageName: String?) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(intent)
    }}