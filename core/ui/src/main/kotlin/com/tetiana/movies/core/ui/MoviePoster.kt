package com.tetiana.movies.core.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.tetiana.movies.core.ui.theme.MoviesTheme
import com.tetiana.movies.core.ui.theme.PreviewComponent

@Composable
fun MoviePoster(posterUrl: String?, modifier: Modifier = Modifier) {
    val placeholder = painterResource(R.drawable.poster_placeholder)
    AsyncImage(
        model = posterUrl,
        contentDescription = null,
        placeholder = placeholder,
        error = placeholder,
        fallback = placeholder,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}

@PreviewComponent
@Composable
private fun MoviePosterPreview() {
    MoviesTheme {
        Surface {
            MoviePoster(
                posterUrl = null,
                modifier = Modifier.padding(16.dp).size(width = 88.dp, height = 132.dp),
            )
        }
    }
}
