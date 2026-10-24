package com.tetiana.movies.feature.search.impl.data.mapper

import com.tetiana.movies.core.domain.error.CommonException
import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.domain.model.MovieType
import com.tetiana.movies.feature.search.impl.data.remote.dto.OmdbMovieDto
import com.tetiana.movies.feature.search.impl.data.remote.dto.OmdbMovieType

private const val MISSING_POSTER = "N/A"

internal fun OmdbMovieDto.toDomain(): Movie =
    Movie(
        imdbId = required(imdbId),
        title = required(title),
        year = required(year),
        type = when (type) {
            OmdbMovieType.MOVIE -> MovieType.MOVIE
            OmdbMovieType.SERIES -> MovieType.SERIES
            OmdbMovieType.EPISODE -> MovieType.EPISODE
            null -> throw CommonException.InvalidData()
        },
        posterUrl = poster?.takeUnless { it.isBlank() || it == MISSING_POSTER },
    )

private fun required(value: String?) =
    value?.takeIf { it.isNotBlank() } ?: throw CommonException.InvalidData()
