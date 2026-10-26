package com.tetiana.movies.feature.search.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tetiana.movies.core.domain.error.CommonException
import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.domain.resources.ResourceProvider
import com.tetiana.movies.core.ui.R as UiR
import com.tetiana.movies.core.ui.message.UserMessage
import com.tetiana.movies.feature.favorites.domain.model.FavoriteChange
import com.tetiana.movies.feature.favorites.domain.usecase.ObserveFavoritesUseCase
import com.tetiana.movies.feature.favorites.domain.usecase.UpdateFavoriteUseCase
import com.tetiana.movies.feature.search.domain.error.CatalogException
import com.tetiana.movies.feature.search.domain.model.SearchRequest
import com.tetiana.movies.feature.search.domain.model.SearchCursor
import com.tetiana.movies.feature.search.domain.model.SearchFeedPage
import com.tetiana.movies.feature.search.domain.policy.CatalogPaginationPolicy
import com.tetiana.movies.feature.search.domain.policy.CatalogSearchPolicy
import com.tetiana.movies.feature.search.domain.usecase.SearchMoviesUseCase
import com.tetiana.movies.feature.search.impl.R
import com.tetiana.movies.feature.search.impl.presentation.paging.SearchPagingSource
import com.tetiana.movies.sdk.contract.NativeAdProvider
import com.tetiana.movies.sdk.native.NativeAdPlacement
import com.tetiana.movies.sdk.session.NativeAds
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase,
    nativeAdProvider: NativeAdProvider,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
    private val resourceProvider: ResourceProvider,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(
        SearchUiState(status = promptStatus()),
    )
    val uiState = mutableUiState.asStateFlow()

    private val nativeAds = NativeAds(
        nativeAdProvider, viewModelScope, NativeAdPlacement.Default.slotCount,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val results: Flow<PagingData<SearchListItem>> = uiState
        .map { prepareQuery(it.query) }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            flow {
                when (query) {
                    SearchQuery.TooShort ->
                        emit(emptyResults(LoadState.NotLoading(endOfPaginationReached = true)))
                    is SearchQuery.Ready -> {
                        emit(emptyResults(LoadState.Loading))
                        delay(SEARCH_DELAY_MILLIS.milliseconds)
                        emitAll(search(query))
                    }
                }
            }
        }
        .cachedIn(viewModelScope)

    init {
        addCloseable(nativeAds)
        nativeAds.load()
        observeFavorites()
    }

    private fun search(query: SearchQuery.Ready): Flow<PagingData<SearchListItem>> = flow {
        coroutineScope {
            val adsEligible = MutableStateFlow(false)
            val pages = Pager(
                config = PagingConfig(
                    pageSize = CatalogPaginationPolicy.PAGE_SIZE,
                    initialLoadSize = CatalogPaginationPolicy.PAGE_SIZE,
                    prefetchDistance = PREFETCH_DISTANCE,
                    enablePlaceholders = false,
                ),
                pagingSourceFactory = {
                    adsEligible.value = false
                    SearchPagingSource { cursor -> loadPage(query, cursor, adsEligible) }
                },
            ).flow.cachedIn(this)

            emitAll(combine(pages, nativeAds.items, adsEligible) { page, ads, eligible ->
                page.withNativeAds(ads, eligible)
            })
        }
    }

    private suspend fun loadPage(
        query: SearchQuery.Ready,
        cursor: SearchCursor,
        adsEligible: MutableStateFlow<Boolean>,
    ): Result<SearchFeedPage> {
        val isFirstPage = cursor == SearchCursor.First
        mutableUiState.update { current ->
            if (prepareQuery(current.query) != query) current
            else current.copy(
                status = if (isFirstPage && current.status != null) loadingStatus() else current.status,
                isLoadingMore = !isFirstPage,
                appendErrorMessage = null,
            )
        }
        return searchMoviesUseCase(SearchRequest(query.keyword, cursor))
            .onSuccess { page ->
                adsEligible.value = NativeAdPlacement.Default.canShowAds(page.movieCount)
                mutableUiState.update { current ->
                    if (prepareQuery(current.query) != query) current
                    else current.copy(
                        status = when {
                            page.movieCount > 0 -> null
                            page.nextCursor == null -> emptyStatus()
                            else -> loadingStatus()
                        },
                        isLoadingMore = false,
                        appendErrorMessage = null,
                    )
                }
            }
            .onFailure { error ->
                mutableUiState.update { current ->
                    if (prepareQuery(current.query) != query) current
                    else current.copy(
                        status = if (isFirstPage && current.status != null) {
                            SearchStatusState(
                                titleRes = R.string.search_error_title,
                                message = searchErrorMessage(error),
                                canRetry = true,
                            )
                        } else current.status,
                        isLoadingMore = false,
                        appendErrorMessage = if (isFirstPage) null else searchErrorMessage(error),
                    )
                }
            }
    }

    fun onQueryChanged(query: String) {
        mutableUiState.update { current ->
            val prepared = prepareQuery(query)
            if (prepared == prepareQuery(current.query)) current.copy(query = query)
            else current.copy(
                query = query,
                canSearch = prepared is SearchQuery.Ready,
                status = if (prepared is SearchQuery.Ready) loadingStatus() else promptStatus(),
                isLoadingMore = false,
                appendErrorMessage = null,
            )
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoritesUseCase().collect { result ->
                result.fold(
                    onSuccess = { movies ->
                        mutableUiState.update { it.copy(favoriteIds = movies.map(Movie::imdbId).toSet()) }
                    },
                    onFailure = {
                        mutableUiState.update {
                            it.copy(
                                favoriteIds = null,
                                message = UserMessage(UiR.string.favorites_read_error, null),
                            )
                        }
                    },
                )
            }
        }
    }

    fun setFavorite(movie: Movie, favorite: Boolean) {
        val id = movie.imdbId
        if (mutableUiState.value.favoriteIds == null || id in mutableUiState.value.savingFavoriteIds) return
        val action = FavoriteChange(movie, favorite)
        mutableUiState.update { it.copy(savingFavoriteIds = it.savingFavoriteIds + id, message = null) }
        viewModelScope.launch {
            try {
                val result = updateFavoriteUseCase(movie, favorite)
                if (result.isFailure) {
                    mutableUiState.update {
                        it.copy(message = UserMessage(UiR.string.favorites_write_error, action))
                    }
                } else {
                    mutableUiState.first { it.favoriteIds == null || (id in it.favoriteIds) == favorite }
                }
            } finally {
                mutableUiState.update { it.copy(savingFavoriteIds = it.savingFavoriteIds - id) }
            }
        }
    }

    fun handleMessageAction(message: UserMessage<FavoriteChange>) {
        if (mutableUiState.value.message?.id != message.id) return
        dismissMessage(message.id)
        message.action?.let { setFavorite(it.movie, it.isFavorite) }
    }

    fun dismissMessage(id: String) {
        mutableUiState.update { if (it.message?.id == id) it.copy(message = null) else it }
    }

    private fun promptStatus() = SearchStatusState(
        titleRes = R.string.search_prompt_title,
        message = resourceProvider.getString(R.string.search_prompt_message),
    )

    private fun emptyStatus() = SearchStatusState(
        titleRes = R.string.search_empty_title,
        message = resourceProvider.getString(R.string.search_empty_message),
    )

    private fun loadingStatus() = SearchStatusState(
        titleRes = R.string.search_loading,
        isLoading = true,
    )

    private fun searchErrorMessage(error: Throwable): String {
        if (error is CatalogException.Backend && !error.message.isNullOrBlank()) {
            return error.message.orEmpty()
        }
        val messageRes = when (error) {
            is CatalogException.InvalidQuery -> R.string.search_prompt_message
            is CatalogException.InvalidPage -> R.string.search_error_invalid_page
            is CatalogException.MissingApiKey -> R.string.search_error_configuration
            is CommonException.Network -> R.string.search_error_network
            is CommonException.InvalidData -> R.string.search_error_invalid_data
            else -> R.string.search_error_fallback
        }
        return resourceProvider.getString(messageRes)
    }

    private fun prepareQuery(rawQuery: String): SearchQuery {
        val query = CatalogSearchPolicy.prepare(rawQuery)
        return if (CatalogSearchPolicy.canSearch(query)) SearchQuery.Ready(query) else SearchQuery.TooShort
    }

    private fun emptyResults(refresh: LoadState): PagingData<SearchListItem> =
        PagingData.empty(
            sourceLoadStates =
                LoadStates(
                    refresh = refresh,
                    prepend = LoadState.NotLoading(endOfPaginationReached = true),
                    append = LoadState.NotLoading(endOfPaginationReached = true),
                ),
        )

    private companion object {
        const val SEARCH_DELAY_MILLIS = 300L
        const val PREFETCH_DISTANCE = 1
    }
}
