package com.tetiana.movies.sdk.contract

/** Awaits shared process-owned SDK initialization without transferring ownership to the caller. */
interface AdsInitializer {
    suspend fun initialize()
}
