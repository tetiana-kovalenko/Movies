package com.tetiana.movies.sdk.contract

import kotlinx.coroutines.flow.StateFlow

/** ViewModel-owned native resources. Null slots are loading or have no fill; rows render their content. */
interface NativeAdSession : AutoCloseable {
    val items: StateFlow<List<NativeAdItem?>>
    suspend fun load()
}
