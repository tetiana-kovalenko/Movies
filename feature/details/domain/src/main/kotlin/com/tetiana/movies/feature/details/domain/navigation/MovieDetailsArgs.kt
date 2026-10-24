package com.tetiana.movies.feature.details.domain.navigation

import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.domain.model.MovieType
import kotlinx.serialization.Serializable

/** Selected movie metadata used to restore Details without another request. */
@Serializable
data class MovieDetailsArgs(
    val imdbId: String,
    val title: String,
    val year: String,
    val type: String,
    val posterUrl: String?,
)

fun Movie.toDetailsArgs() =
    MovieDetailsArgs(
        imdbId = imdbId,
        title = title,
        year = year,
        type = type.name.lowercase(),
        posterUrl = posterUrl,
    )

fun MovieDetailsArgs.toMovie() =
    Movie(
        imdbId = imdbId,
        title = title,
        year = year,
        type = MovieType.valueOf(type.uppercase()),
        posterUrl = posterUrl,
    )
