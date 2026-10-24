package com.tetiana.movies.core.domain.usecase

import com.tetiana.movies.core.domain.error.DomainException
import java.util.logging.Level
import java.util.logging.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

private const val UNEXPECTED_FLOW_USE_CASE_FAILURE = "Unexpected flow use-case failure"

/** Creates the operation per collector; an upstream exception emits one terminal failed Result. */
abstract class BaseFlowUseCase<in Params, T> {
    private val logger = Logger.getLogger(javaClass.name)

    operator fun invoke(params: Params): Flow<Result<T>> = flow {
        emitAll(execute(params).map { Result.success(it) })
    }.catch { failure ->
        when (failure) {
            is CancellationException -> throw failure
            is Exception -> {
                if (failure !is DomainException) logger.log(Level.SEVERE, UNEXPECTED_FLOW_USE_CASE_FAILURE, failure)
                emit(Result.failure(failure))
            }
            else -> throw failure
        }
    }

    protected abstract fun execute(params: Params): Flow<T>
}
