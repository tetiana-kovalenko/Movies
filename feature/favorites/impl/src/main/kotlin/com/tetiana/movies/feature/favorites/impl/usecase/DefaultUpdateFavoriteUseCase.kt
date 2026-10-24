package com.tetiana.movies.feature.favorites.impl.usecase

import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.domain.usecase.BaseUseCase
import com.tetiana.movies.feature.favorites.domain.model.FavoriteChange
import com.tetiana.movies.feature.favorites.domain.repository.FavoritesRepository
import com.tetiana.movies.feature.favorites.domain.usecase.UpdateFavoriteUseCase
import javax.inject.Inject

internal class DefaultUpdateFavoriteUseCase @Inject constructor(
    private val repository: FavoritesRepository,
) : BaseUseCase<FavoriteChange, Unit>(), UpdateFavoriteUseCase {

    override suspend fun invoke(movie: Movie, isFavorite: Boolean): Result<Unit> =
        super.invoke(FavoriteChange(movie, isFavorite))

    override suspend fun execute(params: FavoriteChange) {
        repository.setFavorite(params.movie, params.isFavorite)
    }
}
