package com.tetiana.movies.sdk.contract

/** Immediate SDK handoff result, distinct from confirmed presentation. */
sealed interface FullscreenStart {
    data object Accepted : FullscreenStart
    data object HostUnavailable : FullscreenStart
    data class Failed(val failure: AdFailure) : FullscreenStart
}
