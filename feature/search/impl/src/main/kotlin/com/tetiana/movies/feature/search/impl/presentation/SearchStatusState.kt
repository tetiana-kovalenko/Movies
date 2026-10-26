package com.tetiana.movies.feature.search.impl.presentation

import androidx.annotation.StringRes

/** Full-screen status prepared by the ViewModel; null status in SearchUiState displays results. */
internal data class SearchStatusState(
    @param:StringRes val titleRes: Int,
    val message: String? = null,
    val isLoading: Boolean = false,
    val canRetry: Boolean = false,
)
