package com.tetiana.movies.sdk.session

import android.app.Activity
import com.tetiana.movies.sdk.contract.AdFailure
import com.tetiana.movies.sdk.contract.FullscreenEvents
import com.tetiana.movies.sdk.contract.FullscreenOutcome
import com.tetiana.movies.sdk.contract.FullscreenResource
import com.tetiana.movies.sdk.contract.FullscreenStart
import com.tetiana.movies.sdk.contract.PreparedFullscreenAd
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

/** Main-confined ownership; accepted presentations outlive the coroutine waiting for dismissal. */
internal class OwnedFullscreenAd(
    private val resource: FullscreenResource,
    private val admission: FullscreenAdmission,
) : PreparedFullscreenAd {
    private var state: State = State.Prepared

    override suspend fun show(host: Activity, onPresented: () -> Unit): FullscreenOutcome {
        try {
            currentCoroutineContext().ensureActive()
            if (state != State.Prepared) return FullscreenOutcome.Consumed
            if (!resource.isValid()) return FullscreenOutcome.Expired
            if (!admission.acquire()) return FullscreenOutcome.Busy

            val presenting = State.Presenting(onPresented)
            // Main has no suspension between admission and handoff. Once handed off, only SDK
            // termination may release the resource and admission, even if this caller is cancelled.
            state = presenting
            val events = object : FullscreenEvents {
                override fun onPresented() {
                    if (state !== presenting || presenting.confirmed) return
                    presenting.confirmed = true
                    presenting.onPresented()
                }
                override fun onDismissed() = finish(presenting, FullscreenOutcome.Dismissed)
                override fun onFailed(failure: AdFailure) =
                    finish(presenting, FullscreenOutcome.Failed(failure))
            }
            val start = try {
                resource.present(host, events)
            } catch (failure: Throwable) {
                // No accepted overlay exists when the provider throws during handoff.
                release(presenting)
                throw failure
            }
            when (start) {
                FullscreenStart.Accepted -> Unit
                FullscreenStart.HostUnavailable -> finish(presenting, FullscreenOutcome.HostUnavailable)
                is FullscreenStart.Failed -> finish(presenting, FullscreenOutcome.Failed(start.failure))
            }
            return presenting.terminal.await()
        } finally {
            close()
        }
    }

    override fun close() {
        if (state != State.Prepared) return
        state = State.Released
        resource.close()
    }

    private fun finish(presenting: State.Presenting, outcome: FullscreenOutcome) {
        if (state !== presenting) return // SDKs may repeat terminal events.
        try { release(presenting) } finally { presenting.terminal.complete(outcome) }
    }

    private fun release(presenting: State.Presenting) {
        if (state !== presenting) return
        state = State.Released
        try { resource.close() } finally { admission.release() }
    }

    private sealed interface State {
        data object Prepared : State
        data object Released : State
        class Presenting(val onPresented: () -> Unit) : State {
            val terminal = CompletableDeferred<FullscreenOutcome>()
            var confirmed = false
        }
    }
}
