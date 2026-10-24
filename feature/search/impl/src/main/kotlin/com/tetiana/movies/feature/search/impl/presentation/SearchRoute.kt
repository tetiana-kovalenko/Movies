package com.tetiana.movies.feature.search.impl.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.ui.message.MessageScaffold

@Composable
fun SearchRoute(
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val results = viewModel.results.collectAsLazyPagingItems()
    val resources = LocalResources.current
    MessageScaffold(
        message = state.message,
        onAction = viewModel::handleMessageAction,
        onDismiss = viewModel::dismissMessage,
        modifier = modifier,
    ) { padding ->
        SearchScreen(
            state = viewModel.screenState(state, results.loadState, results.itemCount, resources),
            onSetFavorite = viewModel::setFavorite,
            results = results,
            onQueryChanged = viewModel::onQueryChanged,
            onMovieClick = onMovieClick,
            modifier = Modifier.padding(padding),
        )
    }
}
