package com.ibexia.appauditor.model

//data class AppInfo(
//    val appName: String,
//    val packageName: String,
//    val permissions: String,
//    val riskLevel: RiskLevel,
//    val riskSummary: String? = null
//)












//package com.ibexia.appauditor.model
//
import android.graphics.drawable.Drawable

data class AppInfo(
    val appName: String,
    val packageName: String,  // Ensure this is here
    val permissions: List<String>,
    val riskLevel: RiskLevel,
    val riskSummary: String,
    val icon: Drawable? = null // Added nullable icon property with default value
)