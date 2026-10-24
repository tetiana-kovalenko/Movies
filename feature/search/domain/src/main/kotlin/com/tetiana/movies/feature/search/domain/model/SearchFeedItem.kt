package com.tetiana.movies.feature.search.domain.model

import com.tetiana.movies.core.domain.model.Movie

sealed interface SearchFeedItem {
    data class MovieResult(val movie: Movie) : SearchFeedItem

    /** Fixed movie anchors and zero-based native slot identities. */
    enum class AdSlot(val index: Int) : SearchFeedItem {
        AfterFirstMovie(0),
        AfterSecondMovie(1),
    }
}
