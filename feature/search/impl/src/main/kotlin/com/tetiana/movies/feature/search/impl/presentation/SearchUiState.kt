package com.tetiana.movies.feature.search.impl.presentation

import com.tetiana.movies.core.ui.message.UserMessage
import com.tetiana.movies.feature.favorites.domain.model.FavoriteChange

internal data class SearchUiState(
    val query: String = "",
    val canSearch: Boolean = false,
    val status: SearchStatusState? = null,
    val isLoadingMore: Boolean = false,
    val appendErrorMessage: String? = null,
    val favoriteIds: Set<String>? = null,
    val savingFavoriteIds: Set<String> = emptySet(),
    val message: UserMessage<FavoriteChange>? = null,
)
