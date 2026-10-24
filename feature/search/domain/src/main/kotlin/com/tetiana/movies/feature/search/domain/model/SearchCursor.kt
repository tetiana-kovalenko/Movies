package com.tetiana.movies.feature.search.domain.model

import com.tetiana.movies.feature.search.domain.policy.CatalogPaginationPolicy

/** Immutable continuation retained by Paging when retrying a failed page. */
data class SearchCursor(val page: Int, val seenMovieIds: Set<String>) {
    companion object {
        val First = SearchCursor(CatalogPaginationPolicy.FIRST_PAGE, emptySet())
    }
}
