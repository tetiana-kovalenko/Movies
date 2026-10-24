package com.tetiana.movies.feature.favorites.impl.data.mapper

import com.tetiana.movies.core.domain.error.CommonException
import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.domain.model.MovieType
import com.tetiana.movies.feature.favorites.impl.data.FavoriteMovieEntity

private const val TYPE_MOVIE = "movie"
private const val TYPE_SERIES = "series"
private const val TYPE_EPISODE = "episode"

internal fun FavoriteMovieEntity.toDomain(): Movie = Movie(
    imdbId = imdbId,
    title = title,
    year = year,
    type = when (type) {
        TYPE_MOVIE -> MovieType.MOVIE
        TYPE_SERIES -> MovieType.SERIES
        TYPE_EPISODE -> MovieType.EPISODE
        else -> throw CommonException.InvalidData()
    },
    posterUrl = posterUrl,
)

internal fun Movie.toEntity(): FavoriteMovieEntity = FavoriteMovieEntity(
    imdbId = imdbId,
    title = title,
    year = year,
    posterUrl = posterUrl,
    type = when (type) {
        MovieType.MOVIE -> TYPE_MOVIE
        MovieType.SERIES -> TYPE_SERIES
        MovieType.EPISODE -> TYPE_EPISODE
    },
)
