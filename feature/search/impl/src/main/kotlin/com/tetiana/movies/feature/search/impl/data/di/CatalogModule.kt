package com.tetiana.movies.feature.search.impl.data.di

import com.tetiana.movies.feature.search.domain.repository.CatalogRepository
import com.tetiana.movies.feature.search.domain.usecase.SearchMoviesUseCase
import com.tetiana.movies.feature.search.impl.data.DefaultCatalogRepository
import com.tetiana.movies.feature.search.impl.data.remote.OmdbApi
import com.tetiana.movies.feature.search.impl.usecase.DefaultSearchMoviesUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

@Module
@InstallIn(SingletonComponent::class)
internal abstract class CatalogModule {
    @Binds
    @Singleton
    abstract fun bindCatalogRepository(repository: DefaultCatalogRepository): CatalogRepository

    @Binds
    abstract fun searchMovies(implementation: DefaultSearchMoviesUseCase): SearchMoviesUseCase

    companion object {
        private const val BASE_URL = "https://www.omdbapi.com/"
        private const val JSON_MEDIA_TYPE = "application/json"
        private val json = Json { ignoreUnknownKeys = true }

        @Provides
        @Singleton
        fun provideOmdbApi(): OmdbApi =
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(json.asConverterFactory(JSON_MEDIA_TYPE.toMediaType()))
                .build()
                .create(OmdbApi::class.java)
    }
}
