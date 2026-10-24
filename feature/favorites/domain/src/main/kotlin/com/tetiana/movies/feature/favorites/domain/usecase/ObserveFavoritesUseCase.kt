package com.tetiana.movies.feature.favorites.domain.usecase

import com.tetiana.movies.core.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface ObserveFavoritesUseCase {
    operator fun invoke(): Flow<Result<List<Movie>>>
}
