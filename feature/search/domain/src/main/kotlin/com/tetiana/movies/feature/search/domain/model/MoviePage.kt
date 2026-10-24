package com.tetiana.movies.feature.search.domain.model

import com.tetiana.movies.core.domain.model.Movie

data class MoviePage(val movies: List<Movie>, val totalResults: Int, val page: Int)
