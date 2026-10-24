package com.tetiana.movies.feature.search.impl.presentation

/** Full-screen status prepared by the ViewModel; null status in SearchUiState displays results. */
internal data class SearchStatusState(
    val title: String,
    val message: String? = null,
    val loading: Boolean = false,
    val canRetry: Boolean = false,
)
