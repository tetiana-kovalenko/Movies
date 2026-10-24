package com.tetiana.movies.feature.search.impl.presentation

internal sealed interface SearchQuery {
    data object TooShort : SearchQuery

    data class Ready(val keyword: String) : SearchQuery
}
