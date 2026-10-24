package com.tetiana.movies.sdk.contract

/** Persists consumption after SDK-confirmed presentation. Runs in the caller context;
 * failures are Results and cancellation propagates. Loading alone must never invoke this. */
interface RecordLaunchAdShownUseCase {
    suspend operator fun invoke(params: Unit): Result<Unit>
}
