package com.ibexia.appauditor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.ibexia.appauditor.ads.AdMobManager
import com.ibexia.appauditor.ui.NavGraph
import com.ibexia.appauditor.ui.theme.AppAuditorTheme
import com.ibexia.appauditor.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Google Mobile Ads SDK & preload dashboard banner ad immediately
        AdMobManager.initialize(this)
        AdMobManager.preloadDashboardBanner(this)

        setContent {
            AppAuditorTheme {
                NavGraph(viewModel = viewModel) // Use the NavGraph for navigation
            }
        }
    }
}
