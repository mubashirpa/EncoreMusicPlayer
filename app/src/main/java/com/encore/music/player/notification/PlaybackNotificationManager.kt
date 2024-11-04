package com.encore.music.player.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.encore.music.R
import com.encore.music.presentation.ui.activities.MainActivity

private const val NOTIFICATION_ID = 101
private const val NOTIFICATION_CHANNEL_NAME = "Playback"
private const val NOTIFICATION_CHANNEL_ID = "playback"

class PlaybackNotificationManager(
    private val context: Context,
) {
    private val notificationManager: NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    init {
        createNotificationChannel()
    }

    fun startNotificationService(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession?,
    ) {
        buildNotification(mediaSession)
        startForeGroundNotificationService(mediaSessionService)
    }

    private fun startForeGroundNotificationService(mediaSessionService: MediaSessionService) {
        val notification =
            NotificationCompat
                .Builder(context, NOTIFICATION_CHANNEL_ID)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true)
                .build()
        mediaSessionService.startForeground(NOTIFICATION_ID, notification)
    }

    @OptIn(UnstableApi::class)
    private fun buildNotification(mediaSession: MediaSession?) {
        mediaSession?.let {
            PlayerNotificationManager
                .Builder(
                    context,
                    NOTIFICATION_ID,
                    NOTIFICATION_CHANNEL_ID,
                ).setMediaDescriptionAdapter(
                    PlaybackNotificationAdapter(
                        context = context,
                        pendingIntent = getPendingIntent(),
                    ),
                ).setSmallIconResourceId(R.drawable.ic_launcher_foreground)
                .build()
                .also {
                    it.setMediaSessionToken(mediaSession.platformToken)
                    it.setUseFastForwardActionInCompactView(true)
                    it.setUseRewindActionInCompactView(true)
                    it.setUseNextActionInCompactView(true)
                    it.setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    it.setPlayer(mediaSession.player)
                }
        }
    }

    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
            val channel =
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW,
                )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val intent =
            Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
