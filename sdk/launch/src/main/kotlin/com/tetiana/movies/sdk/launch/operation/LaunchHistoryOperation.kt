package com.tetiana.movies.sdk.launch.operation

import kotlinx.coroutines.CancellationException

/** Result boundaries inherit caller context and preserve cancellation and non-Exception failures. */
internal object LaunchHistoryOperation {
    suspend fun <T> run(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (cancelled: CancellationException) {
        throw cancelled
    } catch (failure: Exception) {
        Result.failure(failure)
    }

}
