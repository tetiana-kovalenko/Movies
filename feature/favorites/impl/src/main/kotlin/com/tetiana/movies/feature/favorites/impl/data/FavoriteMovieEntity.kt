package com.tetiana.movies.feature.favorites.impl.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
internal data class FavoriteMovieEntity(
    @PrimaryKey val imdbId: String,
    val title: String,
    val year: String,
    val posterUrl: String?,
    val type: String,
)
