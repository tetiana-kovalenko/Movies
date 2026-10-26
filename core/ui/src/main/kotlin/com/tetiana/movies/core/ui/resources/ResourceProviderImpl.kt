package com.tetiana.movies.core.ui.resources

import android.content.Context
import androidx.annotation.StringRes
import com.tetiana.movies.core.domain.resources.ResourceProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

internal class ResourceProviderImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ResourceProvider {
    override fun getString(@StringRes resId: Int): String = context.getString(resId)

    override fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String =
        context.getString(resId, *formatArgs)
}
