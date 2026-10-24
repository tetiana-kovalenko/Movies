package com.tetiana.movies.sdk.contract

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Renderable content; its session owns and releases the underlying ad. */
interface NativeAdItem {
    @Composable fun Content(modifier: Modifier)
}
