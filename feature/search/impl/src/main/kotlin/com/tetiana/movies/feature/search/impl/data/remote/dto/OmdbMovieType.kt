package com.tetiana.movies.feature.search.impl.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class OmdbMovieType {
    @SerialName("movie") MOVIE,
    @SerialName("series") SERIES,
    @SerialName("episode") EPISODE,
}
