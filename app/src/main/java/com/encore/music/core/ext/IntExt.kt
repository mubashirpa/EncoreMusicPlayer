package com.encore.music.core.ext

import android.content.Context

fun Int.dpToPx(context: Context): Int {
    val density = context.resources.displayMetrics.density
    val pixels = this * density
    return (pixels + 0.5f).toInt()
}
