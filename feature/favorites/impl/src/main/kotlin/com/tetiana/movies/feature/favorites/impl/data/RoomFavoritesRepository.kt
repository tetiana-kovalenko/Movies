package com.tetiana.movies.feature.favorites.impl.data

import android.database.sqlite.SQLiteException
import com.tetiana.movies.core.domain.error.CommonException
import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.feature.favorites.domain.repository.FavoritesRepository
import com.tetiana.movies.feature.favorites.impl.data.mapper.toDomain
import com.tetiana.movies.feature.favorites.impl.data.mapper.toEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

internal class RoomFavoritesRepository @Inject constructor(
    private val dao: FavoriteMovieDao,
) : FavoritesRepository {

    override fun observeFavorites() = dao.observeAll()
        .map { entities -> entities.map { it.toDomain() } }
        .catch { failure ->
            if (failure is SQLiteException) throw CommonException.Storage(failure) else throw failure
        }

    override suspend fun setFavorite(movie: Movie, isFavorite: Boolean) {
        try {
            if (isFavorite) dao.upsert(movie.toEntity()) else dao.delete(movie.imdbId)
        } catch (failure: SQLiteException) {
            throw CommonException.Storage(failure)
        }
    }
}
