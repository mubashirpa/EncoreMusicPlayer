package com.encore.music.player.service

import android.content.Intent
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.encore.music.di.playerModule
import com.encore.music.player.notification.PlaybackNotificationManager
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

@UnstableApi
class PlaybackService : MediaSessionService() {
    private val player: ExoPlayer by inject()
    private val notificationManager: PlaybackNotificationManager by inject()
    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSession.Builder(this, player).build()
    }

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

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        pauseAllPlayersAndStopSelf()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        // TODO: Fix this issue, sometimes the system cache some resources when the app is killed.
        // So when the app opens again the player wont be re initialized.
        unloadKoinModules(playerModule)
        loadKoinModules(playerModule)
        super.onDestroy()
    }
}
