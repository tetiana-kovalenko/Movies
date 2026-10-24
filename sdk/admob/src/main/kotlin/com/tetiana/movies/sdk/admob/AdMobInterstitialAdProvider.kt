package com.tetiana.movies.sdk.admob

import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.tetiana.movies.sdk.contract.AdFailure
import com.tetiana.movies.sdk.contract.AdLoadOutcome
import com.tetiana.movies.sdk.contract.FullscreenAdLoader
import com.tetiana.movies.sdk.contract.FullscreenResource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine

internal class AdMobInterstitialAdProvider(
    private val initializer: AdMobInitializer,
    private val adUnitId: String,
) : FullscreenAdLoader {

    override suspend fun load(): Result<AdLoadOutcome<FullscreenResource>> {
        try {
            initializer.initialize()
            return suspendCancellableCoroutine { continuation ->
                InterstitialAd.load(
                    AdRequest.Builder(adUnitId).build(),
                    object : AdLoadCallback<InterstitialAd> {
                        override fun onAdLoaded(ad: InterstitialAd) {
                            AdMobMainThread.execute {
                                val resource = AdMobFullscreenResource(ad)
                                continuation.resume(
                                    Result.success(AdLoadOutcome.Ready(resource)),
                                    onCancellation = { _, _, _ -> AdMobMainThread.execute(resource::close) },
                                )
                            }
                        }

                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            val result = if (adError.code == LoadAdError.ErrorCode.NO_FILL) {
                                Result.success(AdLoadOutcome.NoFill)
                            } else {
                                Result.failure(AdFailure("AdMob", adError.code.name, adError.message))
                            }
                            AdMobMainThread.execute {
                                continuation.resume(result, onCancellation = { _, _, _ -> })
                            }
                        }
                    },
                )
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            return Result.failure(AdFailure("AdMob", "INTERSTITIAL_LOAD", "Interstitial request failed", failure))
        }
    }
}
