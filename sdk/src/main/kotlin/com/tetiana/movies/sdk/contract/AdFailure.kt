package com.tetiana.movies.sdk.contract

/** Diagnostic failure independent of any network SDK or application error hierarchy. */
class AdFailure(
    val provider: String,
    val code: String,
    message: String,
    cause: Throwable? = null,
) : Exception(message, cause)
