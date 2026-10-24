package com.tetiana.movies.feature.favorites.domain.usecase

import com.tetiana.movies.core.domain.model.Movie

interface UpdateFavoriteUseCase {
    suspend operator fun invoke(movie: Movie, isFavorite: Boolean): Result<Unit>
}
