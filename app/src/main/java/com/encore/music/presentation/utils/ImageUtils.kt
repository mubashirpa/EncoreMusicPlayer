package com.encore.music.presentation.utils

import android.content.Context
import android.graphics.drawable.Drawable
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.encore.music.R
import com.encore.music.core.ext.dpToPx

object ImageUtils {
    fun loadProfile(
        context: Context,
        url: String,
        onStart: (placeholder: Drawable?) -> Unit,
        onSuccess: (result: Drawable) -> Unit,
        onError: (error: Drawable?) -> Unit,
    ) {
        val imageLoader =
            ImageLoader
                .Builder(context)
                .placeholder(R.drawable.baseline_account_circle_24)
                .error(R.drawable.baseline_account_circle_24)
                .crossfade(true)
                .memoryCache {
                    MemoryCache
                        .Builder(context)
                        .maxSizePercent(0.25)
                        .build()
                }.diskCache {
                    DiskCache
                        .Builder()
                        .directory(context.cacheDir.resolve("image_cache"))
                        .maxSizePercent(0.02)
                        .build()
                }.build()

        val request =
            ImageRequest
                .Builder(context)
                .data(url)
                .transformations(CircleCropTransformation())
                .size(30.dpToPx(context))
                .target(
                    onStart = onStart,
                    onSuccess = onSuccess,
                    onError = onError,
                ).build()
        imageLoader.enqueue(request)
    }
}
