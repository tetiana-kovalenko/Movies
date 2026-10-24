package com.tetiana.movies.feature.search.domain.model

data class SearchRequest(
    val query: String,
    val cursor: SearchCursor = SearchCursor.First,
)
