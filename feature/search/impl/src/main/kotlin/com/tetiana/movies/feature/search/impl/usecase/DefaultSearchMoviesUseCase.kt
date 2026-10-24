package com.tetiana.movies.feature.search.impl.usecase

import com.tetiana.movies.core.domain.usecase.BaseUseCase
import com.tetiana.movies.feature.search.domain.error.CatalogException
import com.tetiana.movies.feature.search.domain.model.SearchRequest
import com.tetiana.movies.feature.search.domain.model.SearchCursor
import com.tetiana.movies.feature.search.domain.model.SearchFeedItem
import com.tetiana.movies.feature.search.domain.model.SearchFeedPage
import com.tetiana.movies.feature.search.domain.policy.CatalogPaginationPolicy
import com.tetiana.movies.feature.search.domain.policy.CatalogSearchPolicy
import com.tetiana.movies.feature.search.domain.repository.CatalogRepository
import com.tetiana.movies.feature.search.domain.usecase.SearchMoviesUseCase
import javax.inject.Inject

/** Validates requests and composes distinct movies and ad anchors without mutating the cursor. */
internal class DefaultSearchMoviesUseCase @Inject constructor(
    private val repository: CatalogRepository,
) : BaseUseCase<SearchRequest, SearchFeedPage>(), SearchMoviesUseCase {
    override suspend fun execute(params: SearchRequest): SearchFeedPage {
        if (!CatalogSearchPolicy.canSearch(params.query)) throw CatalogException.InvalidQuery()
        val cursor = params.cursor
        if (!CatalogPaginationPolicy.isValidPage(cursor.page)) throw CatalogException.InvalidPage()

        val result = repository.search(params.query, cursor.page)
        val seenIds = cursor.seenMovieIds.toMutableSet()
        val items = buildList {
            result.movies.forEach { movie ->
                if (seenIds.add(movie.imdbId)) {
                    add(SearchFeedItem.MovieResult(movie))
                    SearchFeedItem.AdSlot.entries.find { it.index == seenIds.size - 1 }?.let(::add)
                }
            }
        }
        val nextPage = CatalogPaginationPolicy.nextPage(cursor.page, result)
        return SearchFeedPage(
            items = items,
            nextCursor = nextPage?.let { SearchCursor(it, seenIds.toSet()) },
            movieCount = seenIds.size,
        )
    }
}
