package com.tetiana.movies.feature.details.impl.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tetiana.movies.core.domain.model.Movie
import com.tetiana.movies.core.domain.model.MovieType
import com.tetiana.movies.core.ui.FavoriteIconButton
import com.tetiana.movies.core.ui.MoviePoster
import com.tetiana.movies.core.ui.R as UiR
import com.tetiana.movies.core.ui.message.MessageScaffold
import com.tetiana.movies.core.ui.theme.MoviesTheme
import com.tetiana.movies.core.ui.theme.PreviewComponent
import com.tetiana.movies.core.ui.theme.PreviewScreen
import com.tetiana.movies.feature.details.impl.R

private const val POSTER_ASPECT_RATIO = 2f / 3f

@Composable
fun DetailsRoute(viewModel: DetailsViewModel, onBack: () -> Unit, modifier: Modifier = Modifier) {
  val state by viewModel.state.collectAsStateWithLifecycle()
  MessageScaffold(
      message = state.message,
      onAction = viewModel::handleMessageAction,
      onDismiss = viewModel::dismissMessage,
      modifier = modifier,
  ) { padding ->
    DetailsScreen(state, onBack, Modifier.padding(padding), viewModel::updateFavorite)
  }
}

@Composable
internal fun DetailsScreen(
    state: DetailsUiState,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onFavoriteChange: (Boolean) -> Unit = {},
) {
  Column(modifier.fillMaxSize()) {
    DetailsHeader(
        onBack = onBack,
        favoriteAction = {
          FavoriteIconButton(
              isFavorite = state.isFavorite == true,
              enabled = state.isFavorite != null && !state.isSavingFavorite,
              onClick = { onFavoriteChange(state.isFavorite != true) },
          )
        },
    )
    DetailsContent(movie = state.movie, modifier = Modifier.weight(1f))
  }
}

@Composable
private fun DetailsHeader(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    favoriteAction: @Composable () -> Unit,
) {
  Surface(color = MaterialTheme.colorScheme.surfaceContainer, modifier = modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
      IconButton(onClick = onBack) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.details_back),
        )
      }
      Text(
          text = stringResource(R.string.details_title),
          style = MaterialTheme.typography.titleLarge,
          modifier = Modifier
              .weight(1f)
              .padding(horizontal = 12.dp),
      )
      favoriteAction()
    }
  }
}

@Composable
private fun DetailsContent(movie: Movie, modifier: Modifier = Modifier) {
  Column(
      modifier = modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    MoviePoster(
        posterUrl = movie.posterUrl,
        modifier =
            Modifier
                .widthIn(max = 280.dp)
                .fillMaxWidth()
                .aspectRatio(POSTER_ASPECT_RATIO)
                .clip(RoundedCornerShape(24.dp)),
    )
    Text(
        text = movie.title,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
    Text(
        text = movie.year,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(12.dp),
    ) {
      Text(
          text =
              stringResource(
                  when (movie.type) {
                    MovieType.MOVIE -> UiR.string.movie_type_movie
                    MovieType.SERIES -> UiR.string.movie_type_series
                    MovieType.EPISODE -> UiR.string.movie_type_episode
                  }
              ),
          style = MaterialTheme.typography.labelLarge,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      )
    }
  }
}

@PreviewScreen
@Composable
private fun DetailsScreenPreview() {
  MoviesTheme(darkTheme = false) {
    Surface { DetailsScreen(state = DetailsUiState(movie = previewMovie, isFavorite = false), onBack = {}) }
  }
}

@PreviewScreen
@Composable
private fun DetailsScreenDarkPreview() {
  MoviesTheme(darkTheme = true) {
    Surface {
      DetailsScreen(
          state = DetailsUiState(
              movie = previewMovie.copy(title = "A longer movie title that wraps onto several lines"),
              isFavorite = true,
          ),
          onBack = {},
      )
    }
  }
}

@PreviewComponent
@Composable
private fun DetailsHeaderPreview() {
  MoviesTheme {
    DetailsHeader(onBack = {}, favoriteAction = { FavoriteIconButton(true, true, {}) })
  }
}

@PreviewComponent
@Composable
private fun DetailsContentPreview() {
  MoviesTheme {
    Surface { DetailsContent(movie = previewMovie, modifier = Modifier.height(480.dp)) }
  }
}

private val previewMovie =
    Movie(
        imdbId = "tt0000001",
        title = "Love & Other Drugs",
        year = "2010",
        type = MovieType.MOVIE,
        posterUrl = null,
    )
