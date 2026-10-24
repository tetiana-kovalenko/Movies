package com.tetiana.movies.feature.details.impl.presentation

import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.ui.message.UserMessage
import com.tetiana.movies.feature.favorites.domain.model.FavoriteChange

internal data class DetailsUiState(
    val movie: Movie,
    val isFavorite: Boolean? = null,
    val isSavingFavorite: Boolean = false,
    val message: UserMessage<FavoriteChange>? = null,
)
