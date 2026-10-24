package com.tetiana.movies.sdk.session

import android.util.Log
import androidx.annotation.MainThread
import com.tetiana.movies.sdk.contract.NativeAdItem
import com.tetiana.movies.sdk.contract.NativeAdProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Owns one native session for its ViewModel lifetime. Rows render session-owned content. */
@MainThread
class NativeAds(
    provider: NativeAdProvider,
    private val scope: CoroutineScope,
    slotCount: Int,
) : AutoCloseable {
    private val session = if (slotCount > 0) provider.createSession(slotCount) else null
    val items: StateFlow<List<NativeAdItem?>> = session?.items
        ?: MutableStateFlow<List<NativeAdItem?>>(emptyList()).asStateFlow()
    private var loading: Job? = null
    private var closed = false

    /** Starts loading once; SDK callbacks update the session's flow directly. */
    fun load() {
        val session = session ?: return
        if (closed || loading != null) return
        loading = scope.launch {
            try {
                session.load()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                Log.w(TAG, "Native ads failed to load", failure)
            }
        }
    }

    override fun close() {
        if (closed) return
        closed = true
        loading?.cancel()
        session?.close()
    }

    private companion object {
        const val TAG = "NativeAds"
    }
}
