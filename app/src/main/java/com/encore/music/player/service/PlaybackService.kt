package com.encore.music.player.service

import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.encore.music.player.notification.PlaybackNotificationManager
import org.koin.android.ext.android.inject

@UnstableApi
class PlaybackService : MediaSessionService() {
    private val mediaSession: MediaSession by inject()
    private val notificationManager: PlaybackNotificationManager by inject()

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int =
        try {
            notificationManager.startNotificationService(
                mediaSession = mediaSession,
                mediaSessionService = this,
            )
            super.onStartCommand(intent, flags, startId)
        } catch (e: Exception) {
            stopSelf()
            START_NOT_STICKY
        }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession.player
        if (player.playWhenReady) {
            player.pause()
        }
        stopSelf()
    }

    override fun onDestroy() {
        mediaSession.run {
            if (player.isPlaying) {
                player.stop()
            }
            player.release()
            release()
        }
        super.onDestroy()
    }
}
