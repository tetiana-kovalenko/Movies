package com.tetiana.movies.sdk.di

import com.tetiana.movies.sdk.contract.FullscreenAdLoader
import com.tetiana.movies.sdk.contract.FullscreenAdProvider
import com.tetiana.movies.sdk.contract.RecordColdLaunchUseCase
import com.tetiana.movies.sdk.contract.RecordLaunchAdShownUseCase
import com.tetiana.movies.sdk.session.FullscreenAdSessions
import com.tetiana.movies.sdk.session.FullscreenAdmission
import com.tetiana.movies.sdk.session.LaunchAdSession
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
internal object AdsRuntimeModule {
    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @Provides
    fun provideWorkerDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Singleton
    fun provideFullscreenAdProvider(loader: FullscreenAdLoader): FullscreenAdProvider =
        FullscreenAdSessions(loader, FullscreenAdmission())

    @Provides
    @Singleton
    fun provideLaunchAdSession(
        recordColdLaunch: RecordColdLaunchUseCase,
        recordAdShown: RecordLaunchAdShownUseCase,
        provider: FullscreenAdProvider,
        applicationScope: CoroutineScope,
    ): LaunchAdSession = LaunchAdSession(recordColdLaunch, recordAdShown, provider, applicationScope)
}
