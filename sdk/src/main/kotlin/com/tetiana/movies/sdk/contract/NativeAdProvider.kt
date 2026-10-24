package com.tetiana.movies.sdk.contract

interface NativeAdProvider {
    fun createSession(slotCount: Int): NativeAdSession
}
