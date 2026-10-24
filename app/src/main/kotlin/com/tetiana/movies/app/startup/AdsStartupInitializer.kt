package com.tetiana.movies.app.startup

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.startup.Initializer
import com.tetiana.movies.sdk.contract.AdsInitializer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** Runs after Hilt setup, independently of Activity and navigation lifetimes. */
@SuppressLint("EnsureInitializerMetadata")
internal class AdsStartupInitializer : Initializer<AdsInitializer> {
    override fun create(context: Context): AdsInitializer {
        val dependencies = EntryPointAccessors.fromApplication(context, Dependencies::class.java)
        return dependencies.ads.also { ads ->
            dependencies.applicationScope.launch {
                try {
                    ads.initialize()
                } catch (cancelled: CancellationException) {
                    throw cancelled
                } catch (failure: Exception) {
                    Log.w(TAG, "Provider initialization failed", failure)
                }
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Dependencies {
        val ads: AdsInitializer
        val applicationScope: CoroutineScope
    }

    private companion object {
        const val TAG = "AdsStartup"
    }
}
