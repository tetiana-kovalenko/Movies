package com.tetiana.movies.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import com.tetiana.movies.feature.favorites.domain.navigation.FavoritesKey
import com.tetiana.movies.feature.search.domain.navigation.SearchKey

class MoviesNavigationState(val backStack: NavBackStack<NavKey>) {
  val selectedTab: TopLevelDestination
    get() =
        if (FavoritesKey in backStack) TopLevelDestination.Favorites else TopLevelDestination.Search

  val currentDestination: NavKey
    get() = backStack.last()

  val showBottomBar: Boolean
    get() = currentDestination == SearchKey || currentDestination == FavoritesKey

    val canGoBack: Boolean get() = backStack.size > 1

}

@Composable
fun rememberMoviesNavigationState(): MoviesNavigationState {
  val backStack = rememberNavBackStack(SearchKey)
  return remember(backStack) { MoviesNavigationState(backStack) }
}
