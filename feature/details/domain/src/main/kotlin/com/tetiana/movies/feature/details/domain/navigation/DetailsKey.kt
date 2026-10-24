package com.tetiana.movies.feature.details.domain.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("com.tetiana.movies.app.navigation.DetailsKey")
data class DetailsKey(val movie: MovieDetailsArgs) : NavKey
