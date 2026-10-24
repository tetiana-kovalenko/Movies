package com.tetiana.movies.sdk.admob

data class AdMobConfiguration(
    val applicationId: String,
    val nativeAdUnitId: String,
    val interstitialAdUnitId: String,
    val nativeValidatorEnabled: Boolean = false,
)
