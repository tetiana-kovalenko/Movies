package com.tetiana.movies.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.tetiana.movies.core.ui.theme.MoviesTheme
import com.tetiana.movies.core.ui.theme.PreviewComponent

private val FavoriteRed = Color(0xFFE53935)

@Composable
fun FavoriteIconButton(isFavorite: Boolean, enabled: Boolean, onClick: () -> Unit) {
    IconToggleButton(
        checked = isFavorite,
        onCheckedChange = { onClick() },
        enabled = enabled,
    ) {
        Icon(
            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            tint = if (isFavorite) FavoriteRed else LocalContentColor.current,
            contentDescription = stringResource(if (isFavorite) R.string.favorite_remove else R.string.favorite_add),
        )
    }
}

@PreviewComponent
@Composable
private fun FavoriteSavedPreview() = MoviesTheme { FavoriteIconButton(true, true, {}) }

@PreviewComponent
@Composable
private fun FavoriteUnsavedPreview() = MoviesTheme { FavoriteIconButton(false, true, {}) }

@PreviewComponent
@Composable
private fun FavoritePendingPreview() = MoviesTheme { FavoriteIconButton(true, false, {}) }
