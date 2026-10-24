package com.tetiana.movies.feature.search.domain.repository

import com.tetiana.movies.core.domain.error.CommonException
import com.tetiana.movies.feature.search.domain.error.CatalogException
import com.tetiana.movies.feature.search.domain.model.MoviePage

interface CatalogRepository {
    /**
     * Searches the catalog for one page of movies.
     *
     * @throws CatalogException.MissingApiKey when local configuration has no API key.
     * @throws CatalogException.Backend when the successful HTTP body reports a failure,
     * preserving the backend message without interpreting its text.
     * @throws CommonException.Network when the request fails with an I/O error.
     * @throws CommonException.Http for unsuccessful HTTP responses.
     * @throws CommonException.InvalidData for malformed or missing response data.
     */
    suspend fun search(query: String, page: Int): MoviePage
}
