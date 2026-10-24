package com.tetiana.movies.feature.favorites.impl.data.di

import android.content.Context
import androidx.room.Room
import com.tetiana.movies.feature.favorites.domain.repository.FavoritesRepository
import com.tetiana.movies.feature.favorites.domain.usecase.ObserveFavoritesUseCase
import com.tetiana.movies.feature.favorites.domain.usecase.UpdateFavoriteUseCase
import com.tetiana.movies.feature.favorites.impl.data.FavoriteMovieDao
import com.tetiana.movies.feature.favorites.impl.data.MovieDatabase
import com.tetiana.movies.feature.favorites.impl.data.RoomFavoritesRepository
import com.tetiana.movies.feature.favorites.impl.usecase.DefaultObserveFavoritesUseCase
import com.tetiana.movies.feature.favorites.impl.usecase.DefaultUpdateFavoriteUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class FavoritesModule {
    @Binds
    abstract fun bindFavoritesRepository(implementation: RoomFavoritesRepository): FavoritesRepository

    @Binds
    abstract fun bindObserveFavoritesUseCase(implementation: DefaultObserveFavoritesUseCase): ObserveFavoritesUseCase

    @Binds
    abstract fun bindUpdateFavoriteUseCase(implementation: DefaultUpdateFavoriteUseCase): UpdateFavoriteUseCase

    companion object {
        private const val DATABASE_NAME = "movies.db"

        @Provides
        @Singleton
        fun provideMovieDatabase(@ApplicationContext context: Context): MovieDatabase =
            Room.databaseBuilder(context, MovieDatabase::class.java, DATABASE_NAME).build()

        @Provides
        fun provideFavoriteMovieDao(database: MovieDatabase): FavoriteMovieDao = database.favoriteMovies()
    }
}
