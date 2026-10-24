package com.tetiana.movies.core.domain.error

/**
 * Expected domain failure converted to Result.failure by the base use cases.
 * Preserves its diagnostic message and infrastructure cause for callers.
 */
abstract class DomainException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause)
