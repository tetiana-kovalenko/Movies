package com.tetiana.movies.sdk.admob

import android.os.SystemClock
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView
import com.tetiana.movies.sdk.contract.NativeAdItem
import com.tetiana.movies.sdk.contract.NativeAdSession
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** ViewModel-owned AdMob ads. Rows render their content; all access and cleanup run on Main. */
@MainThread
internal class AdMobNativeSession(
    private val initializer: AdMobInitializer,
    private val adUnitId: String,
    slotCount: Int,
) : NativeAdSession {
    private val slots = List(slotCount) { Slot(it) }
    private val mutableItems = MutableStateFlow<List<NativeAdItem?>>(List(slotCount) { null })
    override val items: StateFlow<List<NativeAdItem?>> = mutableItems.asStateFlow()
    private var closed = false

    internal fun ad(slot: Int): NativeAd? = slots[slot].ad

    override suspend fun load() {
        if (slots.isEmpty()) return
        try {
            initializer.initialize()
            if (!closed) slots.forEach(::request)
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            Log.w(TAG, "Native initialization failed", failure)
        }
    }

    private fun request(slot: Slot) {
        try {
            NativeAdLoader.load(
                NativeAdRequest.Builder(adUnitId, listOf(NativeAd.NativeAdType.NATIVE)).build(),
                object : NativeAdLoaderCallback {
                    override fun onNativeAdLoaded(nativeAd: NativeAd) = AdMobMainThread.execute {
                        if (closed) {
                            nativeAd.destroy()
                        } else {
                            slot.loadedAt = SystemClock.elapsedRealtime()
                            slot.ad = nativeAd
                            publish()
                        }
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        if (adError.code != LoadAdError.ErrorCode.NO_FILL) {
                            Log.w(TAG, "Native request failed: ${adError.code}: ${adError.message}")
                        }
                    }
                },
            )
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (failure: Exception) {
            Log.w(TAG, "Native request failed", failure)
        }
    }

    internal fun attach(slotIndex: Int, ad: NativeAd, view: NativeAdView): Boolean {
        val slot = slots[slotIndex]
        if (closed || slot.ad !== ad) return false
        if (SystemClock.elapsedRealtime() - slot.loadedAt >= MAX_CACHE_AGE_MILLIS) {
            release(slot)
            request(slot)
            return false
        }
        // Lazy precomposition can replace a host before its old onRelease callback runs.
        if (slot.view !== view) {
            slot.view?.let(::destroyView)
            slot.view = view
        }
        return true
    }

    internal fun isAttached(slot: Int, ad: NativeAd, view: NativeAdView): Boolean =
        !closed && slots[slot].ad === ad && slots[slot].view === view

    internal fun detach(slot: Int, view: NativeAdView) {
        if (slots[slot].view !== view) return
        slots[slot].view = null
        destroyView(view)
    }

    override fun close() {
        if (closed) return
        closed = true
        slots.forEach(::release)
    }

    private fun release(slot: Slot) {
        slot.view?.let(::destroyView)
        slot.view = null
        slot.ad?.destroy()
        slot.ad = null
        publish()
    }

    private fun destroyView(view: NativeAdView) {
        (view.parent as? ViewGroup)?.removeView(view)
        view.destroy()
    }

    private fun publish() {
        mutableItems.value = slots.map { slot -> slot.takeIf { it.ad != null } }
    }

    private inner class Slot(private val index: Int) : NativeAdItem {
        @Composable
        override fun Content(modifier: Modifier) = NativeAdCard(this@AdMobNativeSession, index, modifier)

        var ad by mutableStateOf<NativeAd?>(null)
        var loadedAt = 0L
        var view: NativeAdView? = null
    }

    private companion object {
        const val TAG = "AdMobNative"
        const val MAX_CACHE_AGE_MILLIS = 60L * 60 * 1_000
    }
}
