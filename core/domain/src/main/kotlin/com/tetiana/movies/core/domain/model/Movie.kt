package com.tetiana.movies.core.domain.model

data class Movie(
    val imdbId: String,
    val title: String,
    val year: String,
    val type: MovieType,
    val posterUrl: String?,
)
