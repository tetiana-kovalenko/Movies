package com.tetiana.movies.feature.search.domain.usecase

import com.tetiana.movies.feature.search.domain.model.SearchRequest
import com.tetiana.movies.feature.search.domain.model.SearchFeedPage

interface SearchMoviesUseCase {
    suspend operator fun invoke(params: SearchRequest): Result<SearchFeedPage>
}
