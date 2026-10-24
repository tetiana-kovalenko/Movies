package com.tetiana.movies.feature.favorites.domain.model

import com.tetiana.movies.core.domain.model.Movie

data class FavoriteChange(val movie: Movie, val isFavorite: Boolean)
