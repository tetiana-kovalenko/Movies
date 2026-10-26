package com.tetiana.movies.feature.search.impl.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.compose.itemContentType
import androidx.paging.compose.collectAsLazyPagingItems
import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.domain.model.MovieType
import com.tetiana.movies.core.ui.FavoriteIconButton
import com.tetiana.movies.core.ui.MovieCard
import com.tetiana.movies.core.ui.R as UiR
import com.tetiana.movies.core.ui.theme.MoviesTheme
import com.tetiana.movies.core.ui.theme.PreviewScreen
import com.tetiana.movies.feature.search.impl.R
import kotlinx.coroutines.flow.flowOf

@Composable
internal fun SearchScreen(
    state: SearchUiState,
    results: LazyPagingItems<SearchListItem>,
    onQueryChanged: (String) -> Unit,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    onSetFavorite: (Movie, Boolean) -> Unit = { _, _ -> },
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val focus = LocalFocusManager.current
    Column(modifier.fillMaxSize().imePadding()) {
        SearchField(
            query = state.query,
            onQueryChanged = onQueryChanged,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        )
        val status = state.status
        if (status != null) {
            SearchStatus(
                state = status,
                onRetry = results::retry,
                modifier = Modifier.weight(1f),
            )
        } else {
            SearchResults(
                state = state,
                onSetFavorite = onSetFavorite,
                results = results,
                onMovieClick = {
                    keyboard?.hide()
                    focus.clearFocus()
                    onMovieClick(it)
                },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val searchLabel = stringResource(R.string.search_placeholder)
    var textFieldValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(query, selection = TextRange(query.length)))
    }
    LaunchedEffect(query) {
        if (textFieldValue.text != query) {
            textFieldValue = TextFieldValue(query, selection = TextRange(query.length))
        }
    }
    TextField(
        value = textFieldValue,
        onValueChange = { value ->
            textFieldValue = value
            onQueryChanged(value.text)
        },
        modifier = modifier.fillMaxWidth()
            .semantics { contentDescription = searchLabel },
        placeholder = { Text(searchLabel) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (textFieldValue.text.isNotEmpty()) {
                IconButton(onClick = {
                    textFieldValue = TextFieldValue()
                    onQueryChanged("")
                }) {
                    Icon(Icons.Default.Close, stringResource(R.string.search_clear))
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(32.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
    )
}

@Composable
private fun SearchStatus(
    state: SearchStatusState,
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.secondaryContainer) {
                Icon(
                    imageVector = if (!state.canRetry) Icons.Default.Search else Icons.Default.Warning,
                    contentDescription = null,
                    modifier = Modifier.padding(20.dp).size(40.dp),
                )
            }
        }
        Text(stringResource(state.titleRes), style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        state.message?.let { message ->
            Text(
                message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        if (state.canRetry) Button(onClick = onRetry) { Text(stringResource(R.string.search_retry)) }
    }
}

@Composable
private fun SearchResults(
    state: SearchUiState,
    results: LazyPagingItems<SearchListItem>,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
    onSetFavorite: (Movie, Boolean) -> Unit = { _, _ -> },
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            count = results.itemCount,
            key = results.itemKey { it.key },
            contentType = results.itemContentType { item ->
                when (item) {
                    is SearchListItem.MovieItem -> CONTENT_TYPE_MOVIE
                    is SearchListItem.AdItem -> CONTENT_TYPE_AD
                }
            },
        ) { index ->
            when (val item = results[index]) {
                is SearchListItem.MovieItem -> {
                    val movie = item.movie
                    MovieCard(
                        title = movie.title,
                        year = movie.year,
                        typeLabel = stringResource(when (movie.type) {
                            MovieType.MOVIE -> UiR.string.movie_type_movie
                            MovieType.SERIES -> UiR.string.movie_type_series
                            MovieType.EPISODE -> UiR.string.movie_type_episode
                        }),
                        posterUrl = movie.posterUrl,
                        onClick = { onMovieClick(movie) },
                        favoriteAction = {
                            val isFavorite = movie.imdbId in state.favoriteIds.orEmpty()
                            FavoriteIconButton(
                                isFavorite = isFavorite,
                                enabled = state.favoriteIds != null && movie.imdbId !in state.savingFavoriteIds,
                                onClick = { onSetFavorite(movie, !isFavorite) },
                            )
                        },
                    )
                }
                is SearchListItem.AdItem -> item.ad.Content(Modifier)
                null -> Unit
            }
        }
        when {
            state.isLoadingMore -> item(key = "loading") {
                val label = stringResource(R.string.search_loading_more)
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp).semantics { contentDescription = label },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) { CircularProgressIndicator() }
            }
            state.appendErrorMessage != null -> item(key = "error") {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        state.appendErrorMessage,
                        textAlign = TextAlign.Center,
                    )
                    OutlinedButton(onClick = results::retry) { Text(stringResource(R.string.search_retry)) }
                }
            }
        }
    }
}

@PreviewScreen
@Composable
private fun SearchPromptPreview() = SearchPreview(SearchUiState(status = previewPromptStatus()))

@PreviewScreen
@Composable
private fun SearchLoadingPreview() = SearchPreview(
    state = SearchUiState(query = "Xen", canSearch = true, status = previewLoadingStatus),
)

@PreviewScreen
@Composable
private fun SearchErrorPreview() = SearchPreview(
    state = SearchUiState(query = "xyz123", canSearch = true, status = previewErrorStatus),
)

@PreviewScreen
@Composable
private fun SearchEmptyPreview() = SearchPreview(
    state = SearchUiState(query = "Xen", canSearch = true, status = previewEmptyStatus()),
)

@PreviewScreen
@Composable
private fun SearchResultsPreview() = SearchPreview(
    state = SearchUiState(query = "Xen", canSearch = true),
    movies = previewMovies,
)

@Composable
private fun SearchPreview(
    state: SearchUiState,
    movies: List<Movie> = emptyList(),
) {
    val items = remember(movies) {
        flowOf(PagingData.from<SearchListItem>(
            data = movies.map { SearchListItem.MovieItem(it) },
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
            ),
        ))
    }.collectAsLazyPagingItems()
    MoviesTheme() {
        Surface {
            SearchScreen(
                state = state,
                results = items,
                onQueryChanged = {},
                onMovieClick = {},
            )
        }
    }
}

@Composable
private fun previewPromptStatus() = SearchStatusState(
    titleRes = R.string.search_prompt_title,
    message = stringResource(R.string.search_prompt_message),
)
private val previewLoadingStatus = SearchStatusState(
    titleRes = R.string.search_loading,
    isLoading = true,
)
private val previewErrorStatus = SearchStatusState(
    titleRes = R.string.search_error_title,
    message = "Movie not found!",
    canRetry = true,
)
@Composable
private fun previewEmptyStatus() = SearchStatusState(
    titleRes = R.string.search_empty_title,
    message = stringResource(R.string.search_empty_message),
)

private val previewMovies = listOf(
    Movie("tt0000001", "Xen", "2024", MovieType.MOVIE, null),
    Movie("tt0000002", "A longer movie title that can wrap onto several lines", "2005–", MovieType.SERIES, null),
    Movie("tt0000003", "Xen", "2015", MovieType.MOVIE, null),
)

private const val CONTENT_TYPE_MOVIE = "movie"
private const val CONTENT_TYPE_AD = "native-ad"
