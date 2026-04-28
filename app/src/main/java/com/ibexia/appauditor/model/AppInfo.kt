package com.ibexia.appauditor.model

data class AppInfo(
    val appName: String,
    val packageName: String,
    val permissions: List<String>,
    val riskLevel: RiskLevel,
    val riskSummary: String
)