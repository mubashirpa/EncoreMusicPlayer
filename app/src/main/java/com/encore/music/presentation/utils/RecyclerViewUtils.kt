package com.encore.music.presentation.utils

import android.content.Context
import android.util.DisplayMetrics
import com.encore.music.core.ext.dpToPx

object SpanCount {
    fun adaptive(
        context: Context,
        minSize: Int,
    ): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels.toFloat()

        var spanCount = (screenWidth / minSize.dpToPx(context)).toInt()

        // Ensure there's at least 1 column
        if (spanCount < 1) spanCount = 1

        return spanCount
    }
}
