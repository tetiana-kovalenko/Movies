package com.tetiana.movies.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.feature.details.domain.navigation.DetailsKey
import com.tetiana.movies.feature.details.domain.navigation.toDetailsArgs
import com.tetiana.movies.feature.details.domain.navigation.toMovie
import com.tetiana.movies.feature.details.impl.presentation.DetailsRoute
import com.tetiana.movies.feature.details.impl.presentation.DetailsViewModel
import com.tetiana.movies.feature.favorites.domain.navigation.FavoritesKey
import com.tetiana.movies.feature.favorites.impl.presentation.FavoritesRoute
import com.tetiana.movies.feature.search.domain.navigation.SearchKey
import com.tetiana.movies.feature.search.impl.presentation.SearchRoute

@Composable
internal fun MoviesNavigation(
    navigation: MoviesNavigationState,
    navigator: MovieNavigator,
    onDetailsOpened: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val currentOnDetailsOpened by rememberUpdatedState(onDetailsOpened)
  val provider =
      remember(navigation, navigator) {
        val onMovieSelected: (Movie) -> Unit = { movie ->
          if (navigation.showBottomBar) {
            navigator.navigate(DetailsKey(movie.toDetailsArgs()))
            currentOnDetailsOpened()
          }
        }

        entryProvider<NavKey> {
          entry<SearchKey> {
            SearchRoute(onMovieClick = onMovieSelected)
          }
          entry<FavoritesKey> {
            FavoritesRoute(onMovieClick = onMovieSelected)
          }
          entry<DetailsKey> { key ->
            val viewModel = hiltViewModel<DetailsViewModel, DetailsViewModel.Factory>(
                creationCallback = { factory -> factory.create(key.movie.toMovie()) },
            )
            DetailsRoute(viewModel = viewModel, onBack = navigator::goBack)
          }
        }
      }
  NavDisplay(
      backStack = navigation.backStack,
      onBack = navigator::goBack,
      modifier = modifier,
      entryDecorators =
          listOf(
              rememberSaveableStateHolderNavEntryDecorator(),
              rememberViewModelStoreNavEntryDecorator(),
          ),
      entryProvider = provider,
  )
}
