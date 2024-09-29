package com.encore.music.core

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    companion object {
        private const val EMPTY = ""
    }

    data class DynamicString(
        val value: String,
    ) : UiText()

    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any,
    ) : UiText()

    data object Empty : UiText()

    fun asString(context: Context): String =
        when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
            Empty -> EMPTY
        }
}
