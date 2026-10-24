package com.tetiana.movies.sdk.contract

/** Loading completed. No fill is an ordinary empty placement, not a failure. */
sealed interface AdLoadOutcome<out R : AdResource> {
    /** Ownership transfers to the caller, who must close the resource. */
    data class Ready<R : AdResource>(val ad: R) : AdLoadOutcome<R>
    data object NoFill : AdLoadOutcome<Nothing>
}
