package com.tetiana.movies.feature.favorites.impl.usecase

import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.domain.usecase.BaseFlowUseCase
import com.tetiana.movies.feature.favorites.domain.repository.FavoritesRepository
import com.tetiana.movies.feature.favorites.domain.usecase.ObserveFavoritesUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class DefaultObserveFavoritesUseCase @Inject constructor(
    private val repository: FavoritesRepository,
) : BaseFlowUseCase<Unit, List<Movie>>(), ObserveFavoritesUseCase {
    override fun invoke(): Flow<Result<List<Movie>>> = super.invoke(Unit)

    override fun execute(params: Unit): Flow<List<Movie>> = repository.observeFavorites()
}
