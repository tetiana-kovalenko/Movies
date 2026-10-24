package com.tetiana.movies.feature.favorites.impl.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FavoriteMovieDao {
    @Query("SELECT * FROM favorite_movies ORDER BY title COLLATE NOCASE, imdbId")
    fun observeAll(): Flow<List<FavoriteMovieEntity>>

    @Upsert
    suspend fun upsert(movie: FavoriteMovieEntity)

    @Query("DELETE FROM favorite_movies WHERE imdbId = :imdbId")
    suspend fun delete(imdbId: String)
}
