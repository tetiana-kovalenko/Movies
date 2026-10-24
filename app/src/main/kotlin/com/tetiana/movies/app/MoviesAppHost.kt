package com.tetiana.movies.app

import android.app.Activity
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.tetiana.movies.sdk.session.LaunchAdSession
import javax.inject.Inject
import kotlinx.coroutines.launch

/** Connects process-owned ad preparation to Activity startup and Details transitions. */
class MoviesAppHost @Inject constructor(private val launchAds: LaunchAdSession) {
    fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) launchAds.start()
    }

    @Composable
    fun Content(activity: Activity) {
        val scope = rememberCoroutineScope()
        MoviesApp(onDetailsOpened = {
            scope.launch { launchAds.presentIfEligible(activity) }
        })
    }
}
