package com.tetiana.movies.sdk.admob.di

import android.app.Application
import com.tetiana.movies.sdk.admob.AdMobConfiguration
import com.tetiana.movies.sdk.admob.AdMobInitializer
import com.tetiana.movies.sdk.admob.AdMobInterstitialAdProvider
import com.tetiana.movies.sdk.admob.AdMobNativeSession
import com.tetiana.movies.sdk.contract.AdsInitializer
import com.tetiana.movies.sdk.contract.FullscreenAdLoader
import com.tetiana.movies.sdk.contract.NativeAdProvider
import com.tetiana.movies.sdk.contract.NativeAdSession
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
internal object AdMobModule {
    @Provides
    @Singleton
    fun provideAdMobInitializer(
        application: Application,
        configuration: AdMobConfiguration,
        applicationScope: CoroutineScope,
        workerDispatcher: CoroutineDispatcher,
    ): AdMobInitializer = AdMobInitializer(
        application,
        configuration.applicationId,
        configuration.nativeValidatorEnabled,
        applicationScope,
        workerDispatcher,
    )

    @Provides
    fun provideAdsInitializer(initializer: AdMobInitializer): AdsInitializer = initializer

    @Provides
    fun provideNativeAdProvider(
        initializer: AdMobInitializer,
        configuration: AdMobConfiguration,
    ): NativeAdProvider = object : NativeAdProvider {
        override fun createSession(slotCount: Int): NativeAdSession =
            AdMobNativeSession(initializer, configuration.nativeAdUnitId, slotCount)
    }

    @Provides
    @Singleton
    fun provideInterstitialAdLoader(
        initializer: AdMobInitializer,
        configuration: AdMobConfiguration,
    ): FullscreenAdLoader = AdMobInterstitialAdProvider(initializer, configuration.interstitialAdUnitId)
}
