package com.tetiana.movies.sdk.contract

/** Terminal presentation outcome; only the separate confirmation event establishes presentation. */
sealed interface FullscreenOutcome {
    data object Dismissed : FullscreenOutcome
    data object Expired : FullscreenOutcome
    data object HostUnavailable : FullscreenOutcome
    data object Busy : FullscreenOutcome
    data object Consumed : FullscreenOutcome
    data class Failed(val failure: AdFailure) : FullscreenOutcome
}
