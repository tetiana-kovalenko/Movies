package com.tetiana.movies.feature.search.impl.data

import com.tetiana.movies.core.domain.error.CommonException
import com.tetiana.movies.feature.search.domain.configuration.OmdbConfiguration
import com.tetiana.movies.feature.search.domain.error.CatalogException
import com.tetiana.movies.feature.search.domain.model.MoviePage
import com.tetiana.movies.feature.search.domain.repository.CatalogRepository
import com.tetiana.movies.feature.search.impl.data.mapper.toDomain
import com.tetiana.movies.feature.search.impl.data.remote.OmdbApi
import com.tetiana.movies.feature.search.impl.data.remote.dto.OmdbResponseStatus
import java.io.IOException
import javax.inject.Inject
import kotlinx.serialization.SerializationException

internal class DefaultCatalogRepository
@Inject
constructor(
    private val api: OmdbApi,
    private val configuration: OmdbConfiguration,
) : CatalogRepository {

  override suspend fun search(query: String, page: Int): MoviePage {
    if (configuration.apiKey.isBlank()) throw CatalogException.MissingApiKey()

    val httpResponse = try {
        api.search(configuration.apiKey, query, page)
    } catch (failure: IOException) {
        throw CommonException.Network(failure)
    } catch (failure: SerializationException) {
        throw CommonException.InvalidData(failure)
    }

    if (!httpResponse.isSuccessful) throw CommonException.Http(httpResponse.code())

    val response = httpResponse.body() ?: throw CommonException.InvalidData()

    return when (response.response) {
        OmdbResponseStatus.SUCCESS -> {
            val movies = response.movies
            val totalResults = response.totalResults?.toIntOrNull()
            if (movies == null || totalResults == null || totalResults < movies.size) throw CommonException.InvalidData()
            MoviePage(movies = movies.map { it.toDomain() }, totalResults = totalResults, page = page)
        }

      OmdbResponseStatus.FAILURE -> throw CatalogException.Backend(response.error)
      else -> throw CommonException.InvalidData()
    }
  }
}
