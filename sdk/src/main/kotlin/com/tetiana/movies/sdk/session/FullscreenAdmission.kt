package com.tetiana.movies.sdk.session

import androidx.annotation.MainThread

/** Share one Main-confined instance across all fullscreen providers in the application runtime. */
@MainThread
internal class FullscreenAdmission {
    private var occupied = false

    internal fun acquire(): Boolean {
        if (occupied) return false
        occupied = true
        return true
    }

    internal fun release() { occupied = false }
}
