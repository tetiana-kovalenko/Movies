package com.tetiana.movies.app.di

import com.tetiana.movies.BuildConfig
import com.tetiana.movies.feature.search.domain.configuration.OmdbConfiguration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CatalogConfigurationModule {
    @Provides
    fun provideOmdbConfiguration(): OmdbConfiguration =
        OmdbConfiguration(BuildConfig.OMDB_API_KEY)
}
