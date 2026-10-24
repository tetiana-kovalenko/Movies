package com.tetiana.movies.core.domain.usecase

import com.tetiana.movies.core.domain.error.DomainException
import java.util.logging.Level
import java.util.logging.Logger
import kotlinx.coroutines.CancellationException

private const val UNEXPECTED_USE_CASE_FAILURE = "Unexpected use-case failure"

/** Converts operation exceptions to Results without choosing a dispatcher. */
abstract class BaseUseCase<in Params, T> {
    private val logger = Logger.getLogger(javaClass.name)

    suspend operator fun invoke(params: Params): Result<T> = try {
        Result.success(execute(params))
    } catch (failure: Throwable) {
        when (failure) {
            is CancellationException -> throw failure
            is Exception -> {
                if (failure !is DomainException) logger.log(Level.SEVERE, UNEXPECTED_USE_CASE_FAILURE, failure)
                Result.failure(failure)
            }
            else -> throw failure
        }
    }

    protected abstract suspend fun execute(params: Params): T
}
