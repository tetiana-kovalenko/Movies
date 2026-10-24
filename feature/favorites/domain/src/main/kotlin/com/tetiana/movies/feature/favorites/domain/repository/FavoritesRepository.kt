package com.tetiana.movies.feature.favorites.domain.repository

import com.tetiana.movies.core.domain.error.CommonException
import com.tetiana.movies.core.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    /**
     * Observes persisted favorites; collection fails with [CommonException.Storage] if reading
     * storage fails, or [CommonException.InvalidData] if persisted metadata is invalid.
     */
    fun observeFavorites(): Flow<List<Movie>>

    /**
     * Adds or removes the movie from persisted favorites.
     *
     * @throws CommonException.Storage when the database write fails.
     */
    suspend fun setFavorite(movie: Movie, isFavorite: Boolean)
}
