package com.tetiana.movies.sdk.launch.domain

import javax.inject.Inject

internal class LaunchAdPolicy @Inject constructor() {
    fun advance(history: AdLaunchHistory): LaunchTransition {
        val count = (history.coldLaunchCount + 1).coerceAtMost(ELIGIBLE_LAUNCH)
        val eligible = !history.adShown && count == ELIGIBLE_LAUNCH
        return LaunchTransition(
            history = history.copy(coldLaunchCount = count),
            eligible = eligible,
        )
    }

    private companion object {
        const val ELIGIBLE_LAUNCH = 2
    }
}
