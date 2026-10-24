package com.tetiana.movies.sdk.contract

import androidx.annotation.MainThread

/** Serialized provider lifecycle events, retained until the SDK reaches a terminal state. */
@MainThread
interface FullscreenEvents {
    fun onPresented()
    fun onDismissed()
    fun onFailed(failure: AdFailure)
}
