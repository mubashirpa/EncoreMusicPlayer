package com.encore.music.player.notification

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerNotificationManager
import coil.ImageLoader
import coil.request.ImageRequest

@UnstableApi
class PlaybackNotificationAdapter(
    private val context: Context,
    private val pendingIntent: PendingIntent?,
) : PlayerNotificationManager.MediaDescriptionAdapter {
    override fun getCurrentContentTitle(player: Player): CharSequence = player.mediaMetadata.title ?: "Unknown"

    override fun createCurrentContentIntent(player: Player): PendingIntent? = pendingIntent

    override fun getCurrentContentText(player: Player): CharSequence? = player.mediaMetadata.artist

    override fun getCurrentSubText(player: Player): CharSequence? = null

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback,
    ): Bitmap? {
        val imageLoader =
            ImageLoader
                .Builder(context)
                .build()
        val request =
            ImageRequest
                .Builder(context)
                .data(player.mediaMetadata.artworkUri)
                .target(
                    onSuccess = { result ->
                        val bitmap = (result as BitmapDrawable).bitmap
                        callback.onBitmap(bitmap)
                    },
                ).build()
        imageLoader.enqueue(request)
        return null
    }
}
