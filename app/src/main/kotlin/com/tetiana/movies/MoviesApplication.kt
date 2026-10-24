package com.tetiana.movies

import android.app.Application
import androidx.startup.AppInitializer
import com.tetiana.movies.app.startup.AdsStartupInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MoviesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppInitializer.getInstance(this).initializeComponent(AdsStartupInitializer::class.java)
    }
}
