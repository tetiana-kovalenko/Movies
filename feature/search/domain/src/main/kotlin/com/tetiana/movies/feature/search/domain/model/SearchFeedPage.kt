package com.tetiana.movies.feature.search.domain.model

/** Composite page with a continuation and the distinct movie count across successful pages. */
data class SearchFeedPage(
    val items: List<SearchFeedItem>,
    val nextCursor: SearchCursor?,
    val movieCount: Int,
)
