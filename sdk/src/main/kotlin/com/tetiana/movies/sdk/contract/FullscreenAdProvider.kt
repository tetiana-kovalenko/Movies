package com.tetiana.movies.sdk.contract

import androidx.annotation.MainThread

/** Prepared-resource boundary. Implementations preserve cancellation and non-Exception failures. */
@MainThread
interface FullscreenAdProvider {
    /**
     * Suspends until ready, no fill or failure. Success transfers ownership; late cancelled results
     * are released. Caller cancellation must not cancel shared provider initialization.
     */
    suspend fun load(): Result<AdLoadOutcome<PreparedFullscreenAd>>
}
