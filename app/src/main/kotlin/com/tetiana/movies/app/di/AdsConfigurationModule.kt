package com.tetiana.movies.app.di

import com.tetiana.movies.BuildConfig
import com.tetiana.movies.sdk.admob.AdMobConfiguration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AdsConfigurationModule {
    @Provides
    fun provideAdMobConfiguration(): AdMobConfiguration =
        AdMobConfiguration(
            applicationId = BuildConfig.ADMOB_APPLICATION_ID,
            nativeAdUnitId = BuildConfig.ADMOB_NATIVE_AD_UNIT_ID,
            interstitialAdUnitId = BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID,
        )
}
