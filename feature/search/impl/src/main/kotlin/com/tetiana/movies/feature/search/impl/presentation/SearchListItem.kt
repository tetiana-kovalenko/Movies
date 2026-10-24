package com.tetiana.movies.feature.search.impl.presentation

import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.sdk.contract.NativeAdItem

sealed interface SearchListItem {
    val key: String

    data class MovieItem(val movie: Movie) : SearchListItem {
        override val key = "movie-${movie.imdbId}"
    }

    data class AdItem(val slot: Int, val ad: NativeAdItem) : SearchListItem {
        override val key = "native-ad-$slot"
    }
}
