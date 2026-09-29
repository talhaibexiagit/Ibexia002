package com.ibexia.appauditor.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdMobManager {
    private const val TAG = "AdMobManager"

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading: Boolean = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading: Boolean = false

    var isInitialized: Boolean = false
        private set

    fun initialize(context: Context) {
        if (isInitialized) return

        // Configure test device whitelist for zero-risk sandboxing
        val requestConfig = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR, "D5C94BAB60A778AE5CAE95AEA654BB1D"))
            .build()
        MobileAds.setRequestConfiguration(requestConfig)

        MobileAds.initialize(context) { status ->
            isInitialized = true
            Log.d(TAG, "Google Mobile Ads SDK initialized successfully")
            // Preload ads in the background
            loadInterstitial(context.applicationContext)
            loadRewarded(context.applicationContext)
        }
    }

    fun loadInterstitial(context: Context) {
        if (interstitialAd != null || isInterstitialLoading) return

        isInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            AdConfig.INTERSTITIAL_AD_UNIT_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "Interstitial ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.w(TAG, "Interstitial ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onDismiss: () -> Unit) {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Interstitial ad dismissed")
                    interstitialAd = null
                    loadInterstitial(activity.applicationContext)
                    onDismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.w(TAG, "Interstitial ad failed to show: ${adError.message}")
                    interstitialAd = null
                    loadInterstitial(activity.applicationContext)
                    onDismiss()
                }
            }
            ad.show(activity)
        } else {
            Log.d(TAG, "Interstitial ad not ready, proceeding with action")
            loadInterstitial(activity.applicationContext)
            onDismiss()
        }
    }

    fun loadRewarded(context: Context) {
        if (rewardedAd != null || isRewardedLoading) return

        isRewardedLoading = true
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            AdConfig.REWARDED_AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    Log.d(TAG, "Rewarded ad loaded successfully")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    rewardedAd = null
                    isRewardedLoading = false
                    Log.w(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                }
            }
        )
    }

    fun showRewarded(
        activity: Activity,
        onRewardEarned: () -> Unit,
        onAdClosed: () -> Unit = {}
    ) {
        val ad = rewardedAd
        if (ad != null) {
            var rewardGranted = false
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Rewarded ad dismissed")
                    rewardedAd = null
                    loadRewarded(activity.applicationContext)
                    if (rewardGranted) {
                        onRewardEarned()
                    }
                    onAdClosed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.w(TAG, "Rewarded ad failed to show: ${adError.message}")
                    rewardedAd = null
                    loadRewarded(activity.applicationContext)
                    onAdClosed()
                }
            }
            ad.show(activity) { rewardItem ->
                Log.d(TAG, "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                rewardGranted = true
            }
        } else {
            Log.d(TAG, "Rewarded ad not loaded yet")
            loadRewarded(activity.applicationContext)
            onAdClosed()
        }
    }

    fun isRewardedAdReady(): Boolean = rewardedAd != null
}
