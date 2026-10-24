package com.tetiana.movies.feature.search.impl.data.remote

import com.tetiana.movies.feature.search.impl.data.remote.dto.OmdbSearchResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

internal interface OmdbApi {
    @GET(".")
    suspend fun search(
        @Query("apikey") apiKey: String,
        @Query("s") query: String,
        @Query("page") page: Int,
    ): Response<OmdbSearchResponseDto>
}
