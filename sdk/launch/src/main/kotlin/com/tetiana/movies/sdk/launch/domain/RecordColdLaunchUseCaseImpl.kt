package com.tetiana.movies.sdk.launch.domain

import com.tetiana.movies.sdk.contract.RecordColdLaunchUseCase
import com.tetiana.movies.sdk.launch.operation.LaunchHistoryOperation
import javax.inject.Inject

internal class RecordColdLaunchUseCaseImpl @Inject constructor(
    private val repository: LaunchHistoryRepository,
    private val policy: LaunchAdPolicy,
) : RecordColdLaunchUseCase {
    override suspend fun invoke(params: Unit): Result<Boolean> = LaunchHistoryOperation.run {
        var eligible = false
        repository.update { history ->
            val transition = policy.advance(history)
            eligible = transition.eligible
            transition.history
        }
        eligible
    }
}
