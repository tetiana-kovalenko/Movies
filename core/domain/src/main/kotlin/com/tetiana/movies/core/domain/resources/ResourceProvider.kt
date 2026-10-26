package com.tetiana.movies.core.domain.resources

/** Provides localized strings without exposing Android resources to consumers. */
interface ResourceProvider {
    fun getString(resId: Int): String

    fun getString(resId: Int, vararg formatArgs: Any?): String
}
