package com.tetiana.movies.core.ui.message

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.tetiana.movies.core.ui.R
import com.tetiana.movies.core.ui.theme.MoviesTheme
import com.tetiana.movies.core.ui.theme.PreviewComponent
import com.tetiana.movies.core.ui.theme.PreviewScreen

/** The route owns message state; this layout reports actions and dismissals by message identity. */
@Composable
fun <Action> MessageScaffold(
    message: UserMessage<Action>?,
    onAction: (UserMessage<Action>) -> Unit,
    onDismiss: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    val hostState = remember { SnackbarHostState() }
    val currentOnAction by rememberUpdatedState(onAction)
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val text = message?.let { stringResource(it.messageRes) }
    val retry = stringResource(R.string.favorites_retry)

    LaunchedEffect(message?.id) {
        if (message != null && text != null) {
            val result = hostState.showSnackbar(
                message = text,
                actionLabel = if (message.action != null) retry else null,
                withDismissAction = true,
                duration = SnackbarDuration.Long,
            )
            if (result == SnackbarResult.ActionPerformed) currentOnAction(message)
            else currentOnDismiss(message.id)
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState) { MessageSnackbar(it) } },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        content = content,
    )
}

@Composable
private fun MessageSnackbar(data: SnackbarData) {
    Snackbar(snackbarData = data, actionOnNewLine = true)
}

@PreviewScreen
@Composable
private fun MessageScaffoldPreview() = MoviesTheme {
    MessageScaffold<Unit>(message = null, onAction = {}, onDismiss = {}) { padding ->
        Box(Modifier.fillMaxSize().padding(padding))
    }
}

@PreviewComponent
@Composable
private fun MessageSnackbarPreview() {
    val text = stringResource(R.string.favorites_write_error)
    val retry = stringResource(R.string.favorites_retry)
    val data = object : SnackbarData {
        override val visuals = object : SnackbarVisuals {
            override val message = text
            override val actionLabel = retry
            override val withDismissAction = true
            override val duration = SnackbarDuration.Long
        }
        override fun performAction() = Unit
        override fun dismiss() = Unit
    }
    MoviesTheme { MessageSnackbar(data) }
}
