package com.tetiana.movies.feature.favorites.impl.data

import androidx.room.Database
import androidx.room.RoomDatabase

/** Persists favorites; schema changes require explicit migrations. */
@Database(entities = [FavoriteMovieEntity::class], version = 1, exportSchema = true)
internal abstract class MovieDatabase : RoomDatabase() {
    abstract fun favoriteMovies(): FavoriteMovieDao
}
