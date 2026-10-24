package com.tetiana.movies.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.tetiana.movies.core.ui.theme.MoviesTheme
import com.tetiana.movies.core.ui.theme.PreviewComponent

private const val POSTER_ASPECT_RATIO = 2f / 3f

@Composable
fun MovieCard(
    title: String,
    year: String,
    typeLabel: String,
    posterUrl: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    favoriteAction: (@Composable () -> Unit)? = null,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier.padding(12.dp).height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MoviePoster(
                posterUrl = posterUrl,
                modifier = Modifier.width(88.dp).aspectRatio(POSTER_ASPECT_RATIO)
                    .clip(RoundedCornerShape(12.dp)),
            )
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f).padding(top = 8.dp),
                    )
                    favoriteAction?.invoke()
                }
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        text = typeLabel,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    year,
                    modifier = Modifier.align(Alignment.End),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@PreviewComponent
@Composable
private fun MovieCardPreview() {
    MoviesTheme(darkTheme = false) {
        Surface {
            MovieCard(
                title = "Xen",
                year = "2024",
                typeLabel = stringResource(R.string.movie_type_movie),
                posterUrl = null,
                onClick = {},
                modifier = Modifier.padding(16.dp),
                favoriteAction = { FavoriteIconButton(false, true, {}) },
            )
        }
    }
}

@PreviewComponent
@Composable
private fun MovieCardLongTitlePreview() {
    MoviesTheme(darkTheme = true) {
        Surface {
            MovieCard(
                title = "A longer movie title that can wrap onto several lines",
                year = "2005–",
                typeLabel = stringResource(R.string.movie_type_series),
                posterUrl = null,
                onClick = {},
                modifier = Modifier.padding(16.dp),
                favoriteAction = { FavoriteIconButton(true, true, {}) },
            )
        }
    }
}
