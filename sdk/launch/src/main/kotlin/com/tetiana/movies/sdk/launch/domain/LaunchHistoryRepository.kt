package com.tetiana.movies.sdk.launch.domain

internal interface LaunchHistoryRepository {
    /** Atomically updates history; expected storage failures throw LaunchHistoryException. */
    suspend fun update(transform: (AdLaunchHistory) -> AdLaunchHistory)
}
