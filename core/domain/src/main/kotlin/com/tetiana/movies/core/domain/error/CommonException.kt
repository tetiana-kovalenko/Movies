package com.tetiana.movies.core.domain.error

private const val NETWORK_MESSAGE = "Network request failed"
private const val INVALID_DATA_MESSAGE = "Invalid response data"
private const val STORAGE_MESSAGE = "Local storage operation failed"
private const val HTTP_MESSAGE = "HTTP request failed"

/** Shared infrastructure failures, retaining diagnostic information independently of UI messages. */
sealed class CommonException(message: String, cause: Throwable? = null) :
    DomainException(message, cause) {
    /** A connection or response-body I/O failure; retains the original [cause]. */
    class Network(cause: Throwable) : CommonException(NETWORK_MESSAGE, cause)

    /** Missing, malformed, or invalid data; [cause] retains a decoding failure when available. */
    class InvalidData(cause: Throwable? = null) : CommonException(INVALID_DATA_MESSAGE, cause)

    /** A local persistence failure; retains the original [cause]. */
    class Storage(cause: Throwable) : CommonException(STORAGE_MESSAGE, cause)

    /** An unsuccessful HTTP response; [statusCode] allows feature-specific interpretation. */
    class Http(val statusCode: Int) : CommonException(HTTP_MESSAGE)
}
