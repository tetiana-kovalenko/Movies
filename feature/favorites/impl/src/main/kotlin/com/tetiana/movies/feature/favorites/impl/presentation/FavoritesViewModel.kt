package com.tetiana.movies.feature.favorites.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.ui.R as UiR
import com.tetiana.movies.core.ui.message.UserMessage
import com.tetiana.movies.feature.favorites.domain.model.FavoriteChange
import com.tetiana.movies.feature.favorites.domain.usecase.ObserveFavoritesUseCase
import com.tetiana.movies.feature.favorites.domain.usecase.UpdateFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
internal class FavoritesViewModel @Inject constructor(
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow(FavoritesUiState())
    val state = mutableState.asStateFlow()

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoritesUseCase().collect { result ->
                result.fold(
                    onSuccess = { movies ->
                        mutableState.update { it.copy(movies = movies, loading = false, favoriteIds = movies.map(Movie::imdbId).toSet()) }
                    },
                    onFailure = {
                        mutableState.update {
                            it.copy(
                                favoriteIds = null,
                                loading = false,
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
        if (mutableState.value.favoriteIds == null || id in mutableState.value.savingFavoriteIds) return
        val action = FavoriteChange(movie, favorite)
        mutableState.update { it.copy(savingFavoriteIds = it.savingFavoriteIds + id, message = null) }
        viewModelScope.launch {
            try {
                val result = updateFavoriteUseCase(movie, favorite)
                if (result.isFailure) {
                    mutableState.update {
                        it.copy(message = UserMessage(UiR.string.favorites_write_error, action))
                    }
                } else {
                    mutableState.first { it.favoriteIds == null || (id in it.favoriteIds) == favorite }
                }
            } finally {
                mutableState.update { it.copy(savingFavoriteIds = it.savingFavoriteIds - id) }
            }
        }
    }

    fun handleMessageAction(message: UserMessage<FavoriteChange>) {
        if (mutableState.value.message?.id != message.id) return
        dismissMessage(message.id)
        message.action?.let { setFavorite(it.movie, it.isFavorite) }
    }

    fun dismissMessage(id: String) {
        mutableState.update { if (it.message?.id == id) it.copy(message = null) else it }
    }

    init {
        observeFavorites()
    }
}
