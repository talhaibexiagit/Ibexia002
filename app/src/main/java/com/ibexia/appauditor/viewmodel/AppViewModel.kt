package com.ibexia.appauditor.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo as AndroidAppInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ibexia.appauditor.model.AppInfo
import com.ibexia.appauditor.model.RiskLevel
import com.ibexia.appauditor.model.ScanUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.HashSet

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val packageManager: PackageManager =
        getApplication<Application>().packageManager

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private val prefs by lazy {
        getApplication<Application>().getSharedPreferences("privasei_prefs", Context.MODE_PRIVATE)
    }

    private val KEY_INITIAL_FREE_PACKAGES = "initial_free_quota_packages"
    private val KEY_UNLOCKED_AD_PACKAGES = "unlocked_ad_packages"
    private val KEY_FIXED_FREE_PACKAGES = "fixed_free_packages"

    // Persistent tracking of freemium package allocations and consumed fixes
    private val initialFreeQuotaPackageNames = mutableSetOf<String>()
    private val unlockedAdPackageNames = mutableSetOf<String>()
    private val fixedFreePackageNames = mutableSetOf<String>()

    // Tracking pending permission revocation in settings
    private var pendingRevokePackage: String? = null
    private var pendingRevokePermissionsCount: Int = -1

    init {
        val savedFree = prefs.getStringSet(KEY_INITIAL_FREE_PACKAGES, null)
        if (!savedFree.isNullOrEmpty()) {
            initialFreeQuotaPackageNames.addAll(savedFree)
        }
        val savedAd = prefs.getStringSet(KEY_UNLOCKED_AD_PACKAGES, null)
        if (!savedAd.isNullOrEmpty()) {
            unlockedAdPackageNames.addAll(savedAd)
        }
        val savedFixed = prefs.getStringSet(KEY_FIXED_FREE_PACKAGES, null)
        if (!savedFixed.isNullOrEmpty()) {
            fixedFreePackageNames.addAll(savedFixed)
        }
    }

    fun isFreeTierApp(packageName: String): Boolean {
        return packageName in initialFreeQuotaPackageNames && packageName !in fixedFreePackageNames
    }

    fun getFreeTierAvailableCount(): Int {
        val available = initialFreeQuotaPackageNames.count { it !in fixedFreePackageNames }
        return maxOf(0, available)
    }

    val permissionRiskMap = mapOf(
        // CRITICAL (Sensitive data access: Camera, Mic, Contacts, SMS, Storage)
        "android.permission.RECORD_AUDIO" to RiskLevel.CRITICAL,
        "android.permission.CAMERA" to RiskLevel.CRITICAL,
        "android.permission.READ_SMS" to RiskLevel.CRITICAL,
        "android.permission.SEND_SMS" to RiskLevel.CRITICAL,
        "android.permission.READ_CONTACTS" to RiskLevel.CRITICAL,
        "android.permission.WRITE_CONTACTS" to RiskLevel.CRITICAL,
        // Storage classified as CRITICAL per product specifications
        "android.permission.READ_EXTERNAL_STORAGE" to RiskLevel.CRITICAL,
        "android.permission.WRITE_EXTERNAL_STORAGE" to RiskLevel.CRITICAL,
        "android.permission.READ_MEDIA_IMAGES" to RiskLevel.CRITICAL,
        "android.permission.READ_MEDIA_VIDEO" to RiskLevel.CRITICAL,
        "android.permission.READ_MEDIA_AUDIO" to RiskLevel.CRITICAL,

        // HIGH (Calendar, Phone/Calls, Location)
        "android.permission.ACCESS_FINE_LOCATION" to RiskLevel.HIGH,
        "android.permission.ACCESS_COARSE_LOCATION" to RiskLevel.HIGH,
        "android.permission.READ_CALENDAR" to RiskLevel.HIGH,
        "android.permission.WRITE_CALENDAR" to RiskLevel.HIGH,
        // Phone / Phone calls: Classified as HIGH risk (matches "Phone" toggle in Android Settings)
        "android.permission.READ_PHONE_STATE" to RiskLevel.HIGH,
        "android.permission.READ_CALL_LOG" to RiskLevel.HIGH,
        "android.permission.CALL_PHONE" to RiskLevel.HIGH,

        // Nearby Devices: Excluded from all risk levels per user specification (general permission, not considered a threat)
        // Original: "android.permission.BLUETOOTH" to RiskLevel.HIGH,
        // Original: "android.permission.BLUETOOTH_CONNECT" to RiskLevel.HIGH,
        // Original: "android.permission.BLUETOOTH_SCAN" to RiskLevel.HIGH,
        // Original: "android.permission.BLUETOOTH_ADVERTISE" to RiskLevel.HIGH,
        // Original: "android.permission.NEARBY_WIFI_DEVICES" to RiskLevel.HIGH,

        // Original: "android.permission.GET_ACCOUNTS" to RiskLevel.HIGH,
        // Commented out: GET_ACCOUNTS is a normal install-time permission on Android 8.0+ (API 26+) and has no revokable toggle in Android Settings.
        // Runtime access to contacts and accounts is controlled via READ_CONTACTS.
        // Original: "android.permission.SYSTEM_ALERT_WINDOW" to RiskLevel.HIGH,
        // Commented out: SYSTEM_ALERT_WINDOW is managed under "Special app access / Appear on top" rather than standard App Permissions.

        // MODERATE
        "android.permission.ACCESS_NETWORK_STATE" to RiskLevel.MODERATE,
        "android.permission.ACCESS_WIFI_STATE" to RiskLevel.MODERATE,

        // LOW
        "android.permission.INTERNET" to RiskLevel.LOW,
        "android.permission.VIBRATE" to RiskLevel.LOW,
        "android.permission.WAKE_LOCK" to RiskLevel.LOW,
        "android.permission.RECEIVE_BOOT_COMPLETED" to RiskLevel.LOW,
        // Notifications: Classified as LOW risk (safe user notifications, not privacy/data-theft threat)
        "android.permission.POST_NOTIFICATIONS" to RiskLevel.LOW
    )

    fun setPremium(isPremium: Boolean) {
        _uiState.value = _uiState.value.copy(isPremium = isPremium)
        if (isPremium && _uiState.value.hasScanned) {
            val actualThreats = _uiState.value.allApps.filter {
                it.riskLevel == RiskLevel.CRITICAL || it.riskLevel == RiskLevel.HIGH
            }.sortedByDescending { it.riskLevel.ordinal }
            _uiState.value = _uiState.value.copy(topThreats = actualThreats)
        }
    }

    fun resetScanState() {
        _uiState.value = _uiState.value.copy(
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
            val startTime = System.currentTimeMillis()

            // Run heavy package analysis on Dispatchers.IO so UI thread never freezes
            val result = withContext(Dispatchers.IO) {
                analyzeApps()
            }
            val allAnalyzedApps = result.second

            // Sort all apps strictly by risk priority: Critical first, then High, then Moderate, then Low
            val sortedAllApps = withContext(Dispatchers.Default) {
                allAnalyzedApps.sortedByDescending { it.riskLevel.ordinal }
            }

            // Filter for actual dangerous threats (CRITICAL or HIGH)
            val actualThreats = sortedAllApps.filter {
                it.riskLevel == RiskLevel.CRITICAL || it.riskLevel == RiskLevel.HIGH
            }

            // Freemium Dynamic Rescan List & Quota Persistence Handling
            val topThreatsToDisplay = if (_uiState.value.isPremium) {
                actualThreats.sortedByDescending { it.riskLevel.ordinal }
            } else {
                if (initialFreeQuotaPackageNames.isEmpty()) {
                    // First scan: initialize with top 3 threats and persist
                    val initialTop3 = actualThreats.take(3)
                    initialFreeQuotaPackageNames.addAll(initialTop3.map { it.packageName })
                    prefs.edit().putStringSet(KEY_INITIAL_FREE_PACKAGES, HashSet(initialFreeQuotaPackageNames)).apply()
                    for (threat in initialTop3) {
                        prefs.edit().putInt("initial_perm_count_" + threat.packageName, threat.permissions.size).apply()
                    }
                } else {
                    // Ensure permission baseline counts are recorded
                    for (pkg in initialFreeQuotaPackageNames) {
                        if (prefs.getInt("initial_perm_count_" + pkg, -1) == -1) {
                            val count = allAnalyzedApps.firstOrNull { it.packageName == pkg }?.permissions?.size ?: -1
                            if (count != -1) {
                                prefs.edit().putInt("initial_perm_count_" + pkg, count).apply()
                            }
                        }
                    }
                }

                // Check if the explicitly revoked package had permissions turned off
                val pendingPkg = pendingRevokePackage
                if (!pendingPkg.isNullOrBlank() && pendingPkg in initialFreeQuotaPackageNames && pendingPkg !in fixedFreePackageNames) {
                    val currentCount = allAnalyzedApps.firstOrNull { it.packageName == pendingPkg }?.permissions?.size ?: 0
                    if (pendingRevokePermissionsCount > 0 && currentCount < pendingRevokePermissionsCount) {
                        fixedFreePackageNames.add(pendingPkg)
                        prefs.edit().putStringSet(KEY_FIXED_FREE_PACKAGES, HashSet(fixedFreePackageNames)).apply()
                    }
                }
                pendingRevokePackage = null

                // General check: if any initial free quota package has fewer permissions than initial baseline
                for (pkg in initialFreeQuotaPackageNames) {
                    if (pkg !in fixedFreePackageNames) {
                        val savedCount = prefs.getInt("initial_perm_count_" + pkg, -1)
                        val currentCount = allAnalyzedApps.firstOrNull { it.packageName == pkg }?.permissions?.size ?: 0
                        if (savedCount != -1 && currentCount < savedCount) {
                            fixedFreePackageNames.add(pkg)
                            prefs.edit().putStringSet(KEY_FIXED_FREE_PACKAGES, HashSet(fixedFreePackageNames)).apply()
                        }
                    }
                }

                // Filter active threats: remaining un-fixed free threats, then ad-unlocked threats
                val remainingFreeThreats = actualThreats.filter { 
                    it.packageName in initialFreeQuotaPackageNames && it.packageName !in fixedFreePackageNames 
                }
                val additionalUnlockedThreats = actualThreats.filter { 
                    it.packageName in unlockedAdPackageNames 
                }

                val combinedThreats = (remainingFreeThreats + additionalUnlockedThreats).distinctBy { it.packageName }

                // High priority risk first: Strictly sort Critical first, then High
                combinedThreats.sortedByDescending { it.riskLevel.ordinal }
            }

            // Ensure smooth scan experience: wait for minimum scan duration so the user sees progress
            val elapsedTime = System.currentTimeMillis() - startTime
            val minScanDuration = 1800L
            if (elapsedTime < minScanDuration) {
                delay(minScanDuration - elapsedTime)
            }

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                hasScanned = true,
                deviceScore = result.first,
                overallRisk = getOverallRiskFromScore(result.first),
                topThreats = topThreatsToDisplay,
                allApps = sortedAllApps
            )
        }
    }

    fun unlockOneMoreApp() {
        val actualThreats = _uiState.value.allApps.filter {
            it.riskLevel == RiskLevel.CRITICAL || it.riskLevel == RiskLevel.HIGH
        }
        val nextLockedApp = actualThreats.firstOrNull {
            it.packageName !in initialFreeQuotaPackageNames && it.packageName !in unlockedAdPackageNames
        }
        if (nextLockedApp != null) {
            unlockedAdPackageNames.add(nextLockedApp.packageName)
            prefs.edit().putStringSet(KEY_UNLOCKED_AD_PACKAGES, HashSet(unlockedAdPackageNames)).apply()

            val remainingFreeThreats = actualThreats.filter { 
                it.packageName in initialFreeQuotaPackageNames && it.packageName !in fixedFreePackageNames 
            }
            val additionalUnlockedThreats = actualThreats.filter { it.packageName in unlockedAdPackageNames }
            val updatedTopThreats = (remainingFreeThreats + additionalUnlockedThreats)
                .distinctBy { it.packageName }
                .sortedByDescending { it.riskLevel.ordinal }

            _uiState.value = _uiState.value.copy(
                unlockedThreatsCount = unlockedAdPackageNames.size,
                topThreats = updatedTopThreats
            )
        } else {
            _uiState.value = _uiState.value.copy(
                unlockedThreatsCount = unlockedAdPackageNames.size
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
            "android.permission.READ_SMS" -> "Reads SMS messages"
            "android.permission.SEND_SMS" -> "Can send SMS"
            "android.permission.READ_CONTACTS",
            "android.permission.WRITE_CONTACTS" -> "Reads contacts and accounts"
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_MEDIA_IMAGES",
            "android.permission.READ_MEDIA_VIDEO",
            "android.permission.READ_MEDIA_AUDIO" -> "Accesses photos, videos and storage"
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION" -> "Tracks device location"
            "android.permission.READ_CALENDAR",
            "android.permission.WRITE_CALENDAR" -> "Accesses calendar"
            // Original: "android.permission.SYSTEM_ALERT_WINDOW" -> "Can draw over other apps"
            "android.permission.READ_PHONE_STATE",
            "android.permission.READ_CALL_LOG",
            "android.permission.CALL_PHONE" -> "Accesses phone and calls"
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
        if (packageName.isNullOrBlank()) return
        try {
            pendingRevokePackage = packageName
            pendingRevokePermissionsCount = getAppPermissions(packageName).size

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            getApplication<Application>().startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
