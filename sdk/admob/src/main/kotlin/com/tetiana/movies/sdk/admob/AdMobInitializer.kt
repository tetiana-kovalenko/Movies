package com.tetiana.movies.sdk.admob

import android.app.Application
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.tetiana.movies.sdk.contract.AdsInitializer
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.suspendCancellableCoroutine

internal class AdMobInitializer(
    application: Application,
    applicationId: String,
    nativeValidatorEnabled: Boolean,
    scope: CoroutineScope,
    dispatcher: CoroutineDispatcher,
) : AdsInitializer {
    private val initialization = scope.async(dispatcher, start = CoroutineStart.LAZY) {
        suspendCancellableCoroutine { continuation ->
            val configuration = InitializationConfig.Builder(applicationId).apply {
                if (!nativeValidatorEnabled) setNativeValidatorDisabled()
            }.build()
            MobileAds.initialize(application, configuration) {
                continuation.resume(Unit)
            }
        }
    }

    override suspend fun initialize() {
        initialization.await()
    }
}
