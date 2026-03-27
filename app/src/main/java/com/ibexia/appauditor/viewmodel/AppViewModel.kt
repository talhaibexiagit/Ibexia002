package com.ibexia.appauditor.viewmodel

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel

class AppViewModel(application: Application) : AndroidViewModel(application) {

    init {
        logAllUserApps()
    }

    private fun logAllUserApps() {
        val pm: PackageManager = getApplication<Application>().packageManager
        val allApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        for (app in allApps) {
            val packageName = app.packageName
            val appName = pm.getApplicationLabel(app).toString()


            val isUserApp = (app.flags and ApplicationInfo.FLAG_SYSTEM) == 0
            val isNotGoogleOrAndroid = !packageName.startsWith("com.android") &&
                    !packageName.startsWith("com.google")
            val isNotSelf = packageName != getApplication<Application>().packageName

            if (isUserApp && isNotGoogleOrAndroid && isNotSelf) {
                Log.d("USER_APPS", "App: $appName, Package: $packageName")
            }
        }
    }
}