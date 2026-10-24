package com.tetiana.movies.sdk.admob

import android.app.Activity
import android.os.SystemClock
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import com.tetiana.movies.sdk.contract.AdFailure
import com.tetiana.movies.sdk.contract.FullscreenEvents
import com.tetiana.movies.sdk.contract.FullscreenResource
import com.tetiana.movies.sdk.contract.FullscreenStart

internal class AdMobFullscreenResource(
    private val ad: InterstitialAd,
) : FullscreenResource {
    private val loadedAt = SystemClock.elapsedRealtime()
    private var closed = false

    override fun isValid(): Boolean = !closed &&
        SystemClock.elapsedRealtime() - loadedAt < INTERSTITIAL_MAX_AGE_MILLIS

    override fun present(host: Activity, events: FullscreenEvents): FullscreenStart {
        val resumed = (host as? LifecycleOwner)?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.RESUMED) == true
        if (host.isFinishing || host.isDestroyed || !resumed) return FullscreenStart.HostUnavailable
        ad.adEventCallback = object : InterstitialAdEventCallback {
            override fun onAdShowedFullScreenContent() = AdMobMainThread.execute(events::onPresented)
            override fun onAdDismissedFullScreenContent() = AdMobMainThread.execute(events::onDismissed)
            override fun onAdFailedToShowFullScreenContent(fullScreenContentError: FullScreenContentError) {
                AdMobMainThread.execute {
                    events.onFailed(AdFailure("AdMob", fullScreenContentError.code.name, fullScreenContentError.message))
                }
            }
        }
        return try {
            ad.show(host)
            FullscreenStart.Accepted
        } catch (failure: RuntimeException) {
            FullscreenStart.Failed(AdFailure("AdMob", "SHOW", "Interstitial presentation failed", failure))
        }
    }

    override fun close() {
        if (closed) return
        closed = true
        ad.adEventCallback = null
        ad.destroy()
    }

    private companion object {
        const val INTERSTITIAL_MAX_AGE_MILLIS = 60L * 60 * 1_000
    }
}
