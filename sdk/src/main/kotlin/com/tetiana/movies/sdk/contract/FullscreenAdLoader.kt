package com.tetiana.movies.sdk.contract

import androidx.annotation.MainThread

/** Provider boundary used by session orchestration, not by feature consumers. */
@MainThread
interface FullscreenAdLoader {
    /** Loads one raw resource, releasing results delivered after cancellation. */
    suspend fun load(): Result<AdLoadOutcome<FullscreenResource>>
}
