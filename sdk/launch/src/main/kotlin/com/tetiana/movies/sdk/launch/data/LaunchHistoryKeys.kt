package com.tetiana.movies.sdk.launch.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey

internal object LaunchHistoryKeys {
    val ColdLaunchCount = intPreferencesKey("launch_count")
    val AdShown = booleanPreferencesKey("was_interstitial_ad_shown")
}
