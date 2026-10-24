package com.tetiana.movies.sdk.contract

import androidx.annotation.MainThread

/** An owned resource. Close idempotently on Main; providers marshal SDK callbacks back to Main. */
@MainThread
interface AdResource : AutoCloseable {
    override fun close()
}
