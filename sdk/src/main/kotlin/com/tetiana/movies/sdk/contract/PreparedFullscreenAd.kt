package com.tetiana.movies.sdk.contract

import android.app.Activity
import androidx.annotation.MainThread

/**
 * Owns one single-use fullscreen ad. Callers must close unused resources.
 * Calls and resource cleanup run on Main; the Activity is retained only during presentation.
 */
@MainThread
interface PreparedFullscreenAd : AdResource {
    /**
     * Consumes this instance even if presentation is rejected; never loads another ad.
     * [onPresented] reports SDK confirmation independently of terminal dismissal and consumer
     * cancellation. The callback must not throw and should hand durable accounting to its own scope.
     * Cancellation before SDK handoff prevents presentation. After handoff the SDK controls the
     * overlay; cancellation stops waiting, not display, accounting or fullscreen admission.
     */
    suspend fun show(host: Activity, onPresented: () -> Unit): FullscreenOutcome
}
