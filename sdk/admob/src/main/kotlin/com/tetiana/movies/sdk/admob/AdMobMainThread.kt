package com.tetiana.movies.sdk.admob

import android.os.Handler
import android.os.Looper

internal object AdMobMainThread {
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    fun execute(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) block() else handler.post(block)
    }
}
