package com.tetiana.movies.sdk.contract

import android.app.Activity
import androidx.annotation.MainThread

/**
 * Raw provider resource. Session orchestration owns consumption and admission. Provider callbacks
 * must be serialized with [present]; Android adapters keep SDK threading inside their boundary.
 */
@MainThread
interface FullscreenResource : AdResource {
    /** Provider-specific validity checked immediately before handoff. */
    fun isValid(): Boolean

    /**
     * Hands this resource to the SDK, or returns a rejection without presenting.
     * Events may arrive synchronously. After acceptance, a terminal event must release ownership;
     * consumers cannot assume the SDK can dismiss an accepted overlay on cancellation.
     */
    fun present(host: Activity, events: FullscreenEvents): FullscreenStart
}
