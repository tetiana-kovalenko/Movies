package com.tetiana.movies.feature.details.impl.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.ui.R as UiR
import com.tetiana.movies.core.ui.message.UserMessage
import com.tetiana.movies.feature.favorites.domain.model.FavoriteChange
import com.tetiana.movies.feature.favorites.domain.usecase.ObserveFavoritesUseCase
import com.tetiana.movies.feature.favorites.domain.usecase.UpdateFavoriteUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Owns the selected movie snapshot and its observed favorite state. */
@HiltViewModel(assistedFactory = DetailsViewModel.Factory::class)
class DetailsViewModel @AssistedInject constructor(
    @Assisted private val movie: Movie,
    private val observeFavoritesUseCase: ObserveFavoritesUseCase,
    private val updateFavoriteUseCase: UpdateFavoriteUseCase,
) : ViewModel() {
    private val mutableState = MutableStateFlow(DetailsUiState(movie = movie))
    internal val state = mutableState.asStateFlow()

    private fun observeFavorites() {
        viewModelScope.launch {
            observeFavoritesUseCase().collect { result ->
                result.fold(
                    onSuccess = { movies ->
                        mutableState.update { it.copy(isFavorite = movies.any { saved -> saved.imdbId == movie.imdbId }) }
                    },
                    onFailure = {
                        mutableState.update {
                            it.copy(
                                isFavorite = null,
                                message = UserMessage(UiR.string.favorites_read_error, null),
                            )
                        }
                    },
                )
            }
        }
    }

    fun updateFavorite(favorite: Boolean) {
        if (mutableState.value.isFavorite == null || mutableState.value.isSavingFavorite) return
        val action = FavoriteChange(movie, favorite)
        mutableState.update { it.copy(isSavingFavorite = true, message = null) }
        viewModelScope.launch {
            try {
                val result = updateFavoriteUseCase(movie, favorite)
                if (result.isFailure) {
                    mutableState.update {
                        it.copy(message = UserMessage(UiR.string.favorites_write_error, action))
                    }
                } else {
                    // Membership is confirmed by observation, never changed optimistically.
                    mutableState.first { it.isFavorite == null || it.isFavorite == favorite }
                }
            } finally {
                mutableState.update { it.copy(isSavingFavorite = false) }
            }
        }
    }

    fun handleMessageAction(message: UserMessage<FavoriteChange>) {
        if (mutableState.value.message?.id != message.id) return
        dismissMessage(message.id)
        message.action?.let { updateFavorite(it.isFavorite) }
    }

    fun dismissMessage(id: String) {
        mutableState.update { if (it.message?.id == id) it.copy(message = null) else it }
    }

    init {
        observeFavorites()
    }

    /** Creates an entry-scoped ViewModel for the navigation snapshot. */
    @AssistedFactory
    interface Factory {
        fun create(movie: Movie): DetailsViewModel
    }
}
