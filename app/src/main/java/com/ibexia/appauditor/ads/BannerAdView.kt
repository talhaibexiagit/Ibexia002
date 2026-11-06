package com.ibexia.appauditor.ads

import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun BannerAdView(
    modifier: Modifier = Modifier,
    screenKey: String = "dashboard"
) {
    val context = LocalContext.current
    val adView = remember(screenKey) {
        if (screenKey == "paywall") {
            AdMobManager.getOrCreatePaywallBanner(context)
        } else {
            AdMobManager.getOrCreateDashboardBanner(context)
        }
    }

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        factory = {
            (adView.parent as? ViewGroup)?.removeView(adView)
            adView
        },
        update = {
            // View is persistent and cached; no need to reload
        }
    )
}
