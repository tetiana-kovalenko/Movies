package com.tetiana.movies.app.navigation

import androidx.annotation.MainThread
import androidx.navigation3.runtime.NavKey
import com.tetiana.movies.feature.favorites.domain.navigation.FavoritesKey

/** Synchronous Main-thread changes to the saved back stack. */
@MainThread
internal class MovieNavigator(private val state: MoviesNavigationState) {
    fun navigate(destination: NavKey) {
        state.backStack.add(destination)
    }

    fun selectTab(tab: TopLevelDestination) {
        if (!state.showBottomBar || state.selectedTab == tab) return
        when (tab) {
            TopLevelDestination.Search -> state.backStack.removeLastOrNull()
            TopLevelDestination.Favorites -> state.backStack.add(FavoritesKey)
        }
    }

    fun goBack() {
        if (state.canGoBack) state.backStack.removeLastOrNull()
    }
}
