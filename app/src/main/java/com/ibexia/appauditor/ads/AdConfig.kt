package com.ibexia.appauditor.ads

object AdConfig {
    // Set to true to use official Google public test IDs (Zero-Risk Sandboxing)
    // Set to false when deploying to production with real AdMob IDs
    const val IS_TEST_MODE: Boolean = true

    // Official Google AdMob Public Test IDs (Never causes account ban)
    const val TEST_APP_ID = "ca-app-pub-3940256099942544~3347511713"
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    const val TEST_REWARDED_AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"

    // Production AdMob Unit IDs (Insert your real AdMob IDs here when ready)
    private const val PROD_BANNER_AD_UNIT_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/YYYYYYYYYY"
    private const val PROD_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/ZZZZZZZZZZ"
    private const val PROD_REWARDED_AD_UNIT_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/AAAAAAAAAA"

    val BANNER_AD_UNIT_ID: String
        get() = if (IS_TEST_MODE) TEST_BANNER_AD_UNIT_ID else PROD_BANNER_AD_UNIT_ID

    val INTERSTITIAL_AD_UNIT_ID: String
        get() = if (IS_TEST_MODE) TEST_INTERSTITIAL_AD_UNIT_ID else PROD_INTERSTITIAL_AD_UNIT_ID

    val REWARDED_AD_UNIT_ID: String
        get() = if (IS_TEST_MODE) TEST_REWARDED_AD_UNIT_ID else PROD_REWARDED_AD_UNIT_ID
}
