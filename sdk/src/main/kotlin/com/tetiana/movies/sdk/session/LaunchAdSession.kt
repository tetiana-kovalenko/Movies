package com.tetiana.movies.sdk.session

import android.app.Activity
import androidx.annotation.MainThread
import com.tetiana.movies.sdk.contract.AdLoadOutcome
import com.tetiana.movies.sdk.contract.FullscreenAdProvider
import com.tetiana.movies.sdk.contract.FullscreenOutcome
import com.tetiana.movies.sdk.contract.PreparedFullscreenAd
import com.tetiana.movies.sdk.contract.RecordColdLaunchUseCase
import com.tetiana.movies.sdk.contract.RecordLaunchAdShownUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Prepares a process-owned interstitial and offers it at content transitions. Call on Main.
 * The application scope keeps preparation and presentation accounting independent of callers.
 */
@MainThread
class LaunchAdSession(
    private val recordColdLaunch: RecordColdLaunchUseCase,
    private val recordAdShown: RecordLaunchAdShownUseCase,
    private val interstitialAds: FullscreenAdProvider,
    private val applicationScope: CoroutineScope,
) {
    private var state: State = State.NotStarted

    /** Records one cold start and prepares asynchronously. Repeated startup signals do nothing. */
    fun start() {
        if (state != State.NotStarted) return
        state = State.Preparing
        applicationScope.launch { prepare() }
    }

    private suspend fun prepare() {
        try {
            val eligible = recordColdLaunch(Unit).getOrDefault(false)
            if (!eligible) return
            when (val outcome = interstitialAds.load().getOrNull()) {
                is AdLoadOutcome.Ready -> {
                    if (currentCoroutineContext().isActive) state = State.Ready(outcome.ad)
                    else outcome.ad.close()
                }
                else -> Unit
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            // Preparation is optional; ordinary provider failures must not block content.
        } finally {
            if (state == State.Preparing) state = State.Finished
        }
    }

    /**
     * Consumes an immediately prepared resource; never waits for loading. Pending history and
     * loading continue independently, allowing presentation at a later transition.
     *
     * @param activity Resumed Activity retained only during presentation.
     * @return `false` if another presentation is active; `true` otherwise.
     * @throws CancellationException Cancels waiting. An accepted overlay and its accounting
     * continue until the SDK reports presentation and termination.
     */
    suspend fun presentIfEligible(activity: Activity): Boolean {
        val previous = state
        if (previous == State.Presenting) return false
        if (previous !is State.Ready) return true
        state = State.Presenting
        try {
            return previous.ad.show(activity) {
                applicationScope.launch { recordAdShown(Unit) }
            } != FullscreenOutcome.Busy
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            return true
        } finally {
            previous.ad.close()
            state = State.Finished
        }
    }

    private sealed interface State {
        data object NotStarted : State
        data object Preparing : State
        class Ready(val ad: PreparedFullscreenAd) : State
        data object Presenting : State
        data object Finished : State
    }
}
