package com.tetiana.movies.feature.favorites.domain.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("com.tetiana.movies.app.navigation.FavoritesKey")
data object FavoritesKey : NavKey
