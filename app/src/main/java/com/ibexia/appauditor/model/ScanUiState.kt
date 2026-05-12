package com.ibexia.appauditor.model

data class ScanUiState(
    val isLoading: Boolean = false,
    val hasScanned: Boolean = false,
    val deviceScore: Int = 0,
    val overallRisk: RiskLevel = RiskLevel.LOW,
    val topThreats: List<AppInfo> = emptyList(),
    val allApps: List<AppInfo> = emptyList()
)