package com.tetiana.movies.sdk.native

/** Native slot count and movie-count display eligibility. */
class NativeAdPlacement(
    val slotCount: Int,
    private val minimumMovies: Int,
) {
    init {
        require(minimumMovies >= 0)
        require(slotCount >= 0)
    }

    /** Display eligibility counts loaded movies only, excluding ads; it never controls loading. */
    fun canShowAds(movieCount: Int): Boolean = movieCount >= minimumMovies

    companion object {
        val Default = NativeAdPlacement(slotCount = 2, minimumMovies = 3)
    }
}
