package com.tetiana.movies.feature.search.domain.policy

import com.tetiana.movies.feature.search.domain.model.MoviePage

object CatalogPaginationPolicy {
  const val FIRST_PAGE = 1
  const val PAGE_SIZE = 10
  private const val LAST_PAGE = 100

  fun isValidPageNumber(page: Int): Boolean = page in FIRST_PAGE..LAST_PAGE

  fun nextPage(page: Int, result: MoviePage): Int? =
      if (
          result.movies.isNotEmpty() && page < LAST_PAGE &&
              page.toLong() * PAGE_SIZE < result.totalResults
      ) page + 1 else null
}
