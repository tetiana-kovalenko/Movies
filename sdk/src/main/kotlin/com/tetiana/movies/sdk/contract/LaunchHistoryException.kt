package com.tetiana.movies.sdk.contract

/**
 * An I/O failure prevented reading or updating advertising launch history.
 * Retains the infrastructure cause; launch-history operations return it as a failed Result.
 */
class LaunchHistoryException(cause: Throwable) : Exception("Could not access advertising history", cause)
