package com.tetiana.movies.feature.search.impl.presentation.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tetiana.movies.feature.search.domain.model.SearchCursor
import com.tetiana.movies.feature.search.domain.model.SearchFeedItem
import com.tetiana.movies.feature.search.domain.model.SearchFeedPage
import kotlinx.coroutines.CancellationException

/** Adapts prepared pages; Paging retains the failed cursor for retry. */
internal class SearchPagingSource(
    private val loadPage: suspend (SearchCursor) -> Result<SearchFeedPage>,
) : PagingSource<SearchCursor, SearchFeedItem>() {
    override fun getRefreshKey(state: PagingState<SearchCursor, SearchFeedItem>): SearchCursor? = null

    override suspend fun load(params: LoadParams<SearchCursor>): LoadResult<SearchCursor, SearchFeedItem> =
        loadPage(params.key ?: SearchCursor.First).fold(
            onSuccess = { page -> LoadResult.Page(page.items, prevKey = null, nextKey = page.nextCursor) },
            onFailure = { failure ->
                if (failure is CancellationException) throw failure
                LoadResult.Error(failure)
            },
        )
}
