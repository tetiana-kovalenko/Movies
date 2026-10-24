package com.tetiana.movies.sdk.session

import androidx.annotation.MainThread
import com.tetiana.movies.sdk.contract.AdLoadOutcome
import com.tetiana.movies.sdk.contract.FullscreenAdLoader
import com.tetiana.movies.sdk.contract.FullscreenAdProvider
import com.tetiana.movies.sdk.contract.PreparedFullscreenAd
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive

/** Adds single consumption and shared admission to a provider. Call on Main. */
@MainThread
internal class FullscreenAdSessions(
    private val loader: FullscreenAdLoader,
    private val admission: FullscreenAdmission,
) : FullscreenAdProvider {
    override suspend fun load(): Result<AdLoadOutcome<PreparedFullscreenAd>> {
        val result = loader.load()
        // Even a provider that returns after cancellation must not leak its delivered resource.
        if (!currentCoroutineContext().isActive) {
            (result.getOrNull() as? AdLoadOutcome.Ready)?.ad?.close()
            currentCoroutineContext().ensureActive()
        }
        return result.map { outcome ->
            when (outcome) {
                AdLoadOutcome.NoFill -> AdLoadOutcome.NoFill
                is AdLoadOutcome.Ready -> AdLoadOutcome.Ready(OwnedFullscreenAd(outcome.ad, admission))
            }
        }
    }
}
