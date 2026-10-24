package com.tetiana.movies.feature.search.domain.error

import com.tetiana.movies.core.domain.error.DomainException

private const val INVALID_QUERY_MESSAGE = "Catalog query is invalid"
private const val INVALID_PAGE_MESSAGE = "Catalog page is invalid"
private const val MISSING_API_KEY_MESSAGE = "OMDb API key is missing"

/** Local catalog validation failures and backend rejections returned in a successful HTTP body. */
sealed class CatalogException(message: String?) : DomainException(message) {
    /** Local validation rejected a query shorter than the catalog search policy allows. */
    class InvalidQuery : CatalogException(INVALID_QUERY_MESSAGE)

    /** Local validation rejected a page outside the supported range. */
    class InvalidPage : CatalogException(INVALID_PAGE_MESSAGE)

    /** Local configuration has no API key; the request has not been sent. */
    class MissingApiKey : CatalogException(MISSING_API_KEY_MESSAGE)

    /**
     * A successful HTTP response reports a failed operation in its body.
     * Preserves the backend [message] without classifying its text; UI supplies a fallback if absent.
     */
    class Backend(message: String?) : CatalogException(message)
}
