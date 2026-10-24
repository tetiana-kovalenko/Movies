package com.tetiana.movies.sdk.contract

/** Atomically records one process cold start and returns persisted eligibility. Call once per process.
 * Runs in the caller context; failures are Results and cancellation propagates. */
interface RecordColdLaunchUseCase {
    suspend operator fun invoke(params: Unit): Result<Boolean>
}
