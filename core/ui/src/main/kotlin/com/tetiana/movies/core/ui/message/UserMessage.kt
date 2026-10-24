package com.tetiana.movies.core.ui.message

import androidx.annotation.StringRes
import java.util.UUID

/** Each occurrence has its own identity so equal messages can be shown again. */
data class UserMessage<out Action>(
    @param:StringRes val messageRes: Int,
    val action: Action?,
    val id: String = UUID.randomUUID().toString(),
)
