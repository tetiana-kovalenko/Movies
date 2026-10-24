package com.tetiana.movies.feature.search.domain.policy

object CatalogSearchPolicy {
  const val MIN_QUERY_LENGTH = 3

  fun prepare(rawQuery: String): String = rawQuery.trim()

  fun canSearch(preparedQuery: String): Boolean = preparedQuery.length >= MIN_QUERY_LENGTH
}
