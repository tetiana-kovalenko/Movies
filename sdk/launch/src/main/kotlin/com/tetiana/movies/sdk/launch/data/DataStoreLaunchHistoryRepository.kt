package com.tetiana.movies.sdk.launch.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.tetiana.movies.sdk.contract.LaunchHistoryException
import com.tetiana.movies.sdk.launch.domain.AdLaunchHistory
import com.tetiana.movies.sdk.launch.domain.LaunchHistoryRepository
import java.io.File
import java.io.IOException
import kotlinx.coroutines.CoroutineScope

internal class DataStoreLaunchHistoryRepository(
    private val dataStore: DataStore<Preferences>,
) : LaunchHistoryRepository {
    companion object {
        private const val HISTORY_DIRECTORY = "datastore"
        private const val HISTORY_FILENAME = "ads_launch_history.preferences_pb"

        /** Creates the process-owned store in [filesDirectory]; [scope] owns its IO work. */
        fun createStore(filesDirectory: File, scope: CoroutineScope): DataStore<Preferences> =
            PreferenceDataStoreFactory.create(scope = scope) { File(File(filesDirectory, HISTORY_DIRECTORY), HISTORY_FILENAME) }
    }

    override suspend fun update(transform: (AdLaunchHistory) -> AdLaunchHistory) {
        try {
            dataStore.edit { preferences ->
                val history = AdLaunchHistory(
                    coldLaunchCount = preferences[LaunchHistoryKeys.ColdLaunchCount] ?: 0,
                    adShown = preferences[LaunchHistoryKeys.AdShown] ?: false,
                )
                val updated = transform(history)
                preferences[LaunchHistoryKeys.ColdLaunchCount] = updated.coldLaunchCount
                preferences[LaunchHistoryKeys.AdShown] = updated.adShown
            }
        } catch (failure: IOException) {
            throw LaunchHistoryException(cause = failure)
        }
    }
}
