package com.ibexia.appauditor.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.ibexia.appauditor.model.RiskLevel

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val packageManager: PackageManager =
        getApplication<Application>().packageManager

    // Permission → Risk mapping
    private val permissionRiskMap = mapOf(
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

    init {
        analyzeApps()
    }

    private fun analyzeApps() {

        val allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        var finalScore = 100f

        val appScores = mutableListOf<Pair<String, RiskLevel>>()

        for (app in allApps) {

            val packageName = app.packageName
            val appName = packageManager.getApplicationLabel(app).toString()

            val isUserApp = (app.flags and ApplicationInfo.FLAG_SYSTEM) == 0
            val isNotGoogleOrAndroid =
                !packageName.startsWith("com.android") &&
                        !packageName.startsWith("com.google")

            val isNotSelf =
                packageName != getApplication<Application>().packageName

            if (isUserApp && isNotGoogleOrAndroid && isNotSelf) {

                val permissions = getAppPermissions(packageName)

                val uniquePermissions = permissions.toSet()

                // ⭐ ONLY highest risk matters (IMPORTANT CHANGE)
                val risk = getHighestRisk(uniquePermissions)

                val appDeduction = when (risk) {
                    RiskLevel.CRITICAL -> 2f
                    RiskLevel.HIGH -> 1f
                    RiskLevel.MODERATE -> 0f
                    RiskLevel.LOW -> 0f
                }

                finalScore -= appDeduction

                appScores.add(appName to risk)

                Log.d(
                    "APP_ANALYSIS",
                    "App: $appName | Risk: $risk | Deduction: -$appDeduction"
                )
            }
        }

        finalScore = maxOf(0f, finalScore)

        for ((name, risk) in appScores) {
            Log.d("APP_SCORE", "App: $name | Final Risk: $risk")
        }

        Log.d("DEVICE_SCORE", "Final Device Score: $finalScore")
    }

    // ⭐ NEW LOGIC: highest risk only
    private fun getHighestRisk(permissions: Set<String>): RiskLevel {

        var highestRisk = RiskLevel.LOW

        for (permission in permissions) {
            val risk = permissionRiskMap[permission] ?: continue

            if (risk.ordinal > highestRisk.ordinal) {
                highestRisk = risk
            }
        }

        return highestRisk
    }

    private fun getAppPermissions(packageName: String): List<String> {
        return try {
            val packageInfo = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_PERMISSIONS
            )
            packageInfo.requestedPermissions?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}