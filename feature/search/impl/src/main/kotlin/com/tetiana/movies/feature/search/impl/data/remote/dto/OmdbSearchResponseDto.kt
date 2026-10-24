package com.tetiana.movies.feature.search.impl.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class OmdbSearchResponseDto(
    @SerialName("Search") val movies: List<OmdbMovieDto>? = null,
    @SerialName("totalResults") val totalResults: String? = null,
    @SerialName("Response") val response: OmdbResponseStatus? = null,
    @SerialName("Error") val error: String? = null,
)
