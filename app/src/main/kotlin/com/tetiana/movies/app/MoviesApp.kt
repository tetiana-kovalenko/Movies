package com.tetiana.movies.app

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tetiana.movies.R
import com.tetiana.movies.app.navigation.MovieNavigator
import com.tetiana.movies.app.navigation.MoviesNavigation
import com.tetiana.movies.app.navigation.TopLevelDestination
import com.tetiana.movies.app.navigation.rememberMoviesNavigationState
import com.tetiana.movies.core.ui.theme.MoviesTheme

@Composable
fun MoviesApp(onDetailsOpened: () -> Unit) {
  val navigation = rememberMoviesNavigationState()
  val navigator = remember(navigation) { MovieNavigator(navigation) }
  MoviesTheme {
    Scaffold(
        bottomBar = {
          if (navigation.showBottomBar)
              NavigationBar {
                NavigationBarItem(
                    selected = navigation.selectedTab == TopLevelDestination.Search,
                    onClick = { navigator.selectTab(TopLevelDestination.Search) },
                    icon = { Icon(Icons.Default.Search, null) },
                    label = { Text(stringResource(R.string.nav_search)) },
                )
                NavigationBarItem(
                    selected = navigation.selectedTab == TopLevelDestination.Favorites,
                    onClick = { navigator.selectTab(TopLevelDestination.Favorites) },
                    icon = { Icon(Icons.Default.Favorite, null) },
                    label = { Text(stringResource(R.string.nav_favorites)) },
                )
              }
        }
    ) { padding ->
      MoviesNavigation(
          navigation = navigation,
          navigator = navigator,
          onDetailsOpened = onDetailsOpened,
          modifier = Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding),
      )
    }
  }
}
