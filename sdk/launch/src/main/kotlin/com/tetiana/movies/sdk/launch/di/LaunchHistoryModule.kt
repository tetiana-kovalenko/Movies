package com.tetiana.movies.sdk.launch.di

import android.app.Application
import com.tetiana.movies.sdk.contract.RecordColdLaunchUseCase
import com.tetiana.movies.sdk.contract.RecordLaunchAdShownUseCase
import com.tetiana.movies.sdk.launch.data.DataStoreLaunchHistoryRepository
import com.tetiana.movies.sdk.launch.domain.LaunchHistoryRepository
import com.tetiana.movies.sdk.launch.domain.RecordColdLaunchUseCaseImpl
import com.tetiana.movies.sdk.launch.domain.RecordLaunchAdShownUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

@Module
@InstallIn(SingletonComponent::class)
internal object LaunchHistoryModule {
    @Provides
    @Singleton
    fun provideHistoryRepository(
        application: Application,
        applicationScope: CoroutineScope,
        workerDispatcher: CoroutineDispatcher,
    ): LaunchHistoryRepository = DataStoreLaunchHistoryRepository(
        dataStore = DataStoreLaunchHistoryRepository.createStore(
            filesDirectory = application.filesDir,
            scope = CoroutineScope(applicationScope.coroutineContext + workerDispatcher),
        ),
    )

    @Provides
    fun provideRecordColdLaunch(implementation: RecordColdLaunchUseCaseImpl): RecordColdLaunchUseCase = implementation

    @Provides
    fun provideRecordAdShown(implementation: RecordLaunchAdShownUseCaseImpl): RecordLaunchAdShownUseCase = implementation
}
