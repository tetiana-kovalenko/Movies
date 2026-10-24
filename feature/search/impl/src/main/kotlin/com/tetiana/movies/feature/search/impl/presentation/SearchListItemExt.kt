package com.tetiana.movies.feature.search.impl.presentation

import androidx.paging.PagingData
import androidx.paging.filter
import androidx.paging.map
import com.tetiana.movies.feature.search.domain.model.SearchFeedItem
import com.tetiana.movies.sdk.contract.NativeAdItem

/** Resolves filled native slots at their fixed movie anchors. */
internal fun PagingData<SearchFeedItem>.withNativeAds(
    ads: List<NativeAdItem?>,
    adsEligible: Boolean,
): PagingData<SearchListItem> {
    val readyAds = if (adsEligible) {
        ads.mapIndexedNotNull { index, ad -> ad?.let { index to it } }.toMap()
    } else emptyMap()

    return filter { it !is SearchFeedItem.AdSlot || it.index in readyAds }
        .map { item ->
            when (item) {
                is SearchFeedItem.MovieResult -> SearchListItem.MovieItem(item.movie)
                is SearchFeedItem.AdSlot -> SearchListItem.AdItem(item.index, readyAds.getValue(item.index))
            }
        }
}
