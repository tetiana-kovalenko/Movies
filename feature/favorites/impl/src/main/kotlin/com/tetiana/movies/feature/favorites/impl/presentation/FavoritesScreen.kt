package com.tetiana.movies.feature.favorites.impl.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.domain.model.MovieType
import com.tetiana.movies.core.ui.FavoriteIconButton
import com.tetiana.movies.core.ui.MovieCard
import com.tetiana.movies.core.ui.R as UiR
import com.tetiana.movies.core.ui.message.MessageScaffold
import com.tetiana.movies.core.ui.theme.MoviesTheme
import com.tetiana.movies.core.ui.theme.PreviewScreen
import com.tetiana.movies.feature.favorites.impl.R

@Composable
fun FavoritesRoute(onMovieClick: (Movie) -> Unit, modifier: Modifier = Modifier) {
    val viewModel: FavoritesViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    MessageScaffold(
        message = state.message,
        onAction = viewModel::handleMessageAction,
        onDismiss = viewModel::dismissMessage,
        modifier = modifier,
    ) { padding ->
        FavoritesScreen(state, onMovieClick, viewModel::setFavorite, Modifier.padding(padding))
    }
}

@Composable
internal fun FavoritesScreen(
    state: FavoritesUiState,
    onMovieClick: (Movie) -> Unit,
    onSetFavorite: (Movie, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text(stringResource(R.string.favorites_title), style = MaterialTheme.typography.headlineSmall) }
        when {
            state.loading -> item { CircularProgressIndicator() }
            state.favoriteIds == null -> Unit
            state.movies.isEmpty() -> item { Text(stringResource(R.string.favorites_empty), style = MaterialTheme.typography.bodyLarge) }
            else -> items(state.movies, key = { it.imdbId }) { movie ->
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
                        FavoriteIconButton(
                            isFavorite = movie.imdbId in state.favoriteIds.orEmpty(),
                            enabled = movie.imdbId !in state.savingFavoriteIds,
                            onClick = { onSetFavorite(movie, false) },
                        )
                    },
                )
            }
        }
    }
}

@PreviewScreen
@Composable
private fun FavoritesContentPreview() = MoviesTheme {
    Surface { FavoritesScreen(FavoritesUiState(movies = listOf(Movie("tt1", "A longer movie title that wraps onto several lines", "2005–", MovieType.SERIES, null)), loading = false, favoriteIds = setOf("tt1")), {}, { _, _ -> }) }
}

@PreviewScreen
@Composable
private fun FavoritesEmptyPreview() = MoviesTheme { Surface { FavoritesScreen(FavoritesUiState(loading = false, favoriteIds = emptySet()), {}, { _, _ -> }) } }

@PreviewScreen
@Composable
private fun FavoritesLoadingPreview() = MoviesTheme { Surface { FavoritesScreen(FavoritesUiState(), {}, { _, _ -> }) } }

@PreviewScreen
@Composable
private fun FavoritesErrorPreview() = MoviesTheme { Surface { FavoritesScreen(FavoritesUiState(loading = false), {}, { _, _ -> }) } }
