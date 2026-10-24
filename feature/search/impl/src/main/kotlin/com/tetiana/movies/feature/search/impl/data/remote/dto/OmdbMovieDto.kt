package com.tetiana.movies.feature.search.impl.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OmdbMovieDto(
    @SerialName("imdbID") val imdbId: String? = null,
    @SerialName("Title") val title: String? = null,
    @SerialName("Year") val year: String? = null,
    @SerialName("Type") val type: OmdbMovieType? = null,
    @SerialName("Poster") val poster: String? = null,
)
