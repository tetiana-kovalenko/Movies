package com.tetiana.movies.feature.favorites.impl.presentation

import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.ui.message.UserMessage
import com.tetiana.movies.feature.favorites.domain.model.FavoriteChange

internal data class FavoritesUiState(
    val movies: List<Movie> = emptyList(),
    val loading: Boolean = true,
    val favoriteIds: Set<String>? = null,
    val savingFavoriteIds: Set<String> = emptySet(),
    val message: UserMessage<FavoriteChange>? = null,
)
