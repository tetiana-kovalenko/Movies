package com.tetiana.movies.sdk.launch.domain

import com.tetiana.movies.sdk.contract.RecordLaunchAdShownUseCase
import com.tetiana.movies.sdk.launch.operation.LaunchHistoryOperation
import javax.inject.Inject

internal class RecordLaunchAdShownUseCaseImpl @Inject constructor(
    private val repository: LaunchHistoryRepository,
) : RecordLaunchAdShownUseCase {
    override suspend fun invoke(params: Unit): Result<Unit> = LaunchHistoryOperation.run {
        repository.update { it.copy(adShown = true) }
    }
}
