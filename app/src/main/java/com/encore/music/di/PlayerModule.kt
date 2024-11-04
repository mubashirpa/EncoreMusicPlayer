package com.encore.music.di

import android.annotation.SuppressLint
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.encore.music.player.notification.PlaybackNotificationManager
import com.encore.music.player.service.PlaybackServiceHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

@SuppressLint("UnsafeOptInUsageError")
val playerModule =
    module {
        single<AudioAttributes> {
            AudioAttributes
                .Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build()
        }
        single<ExoPlayer> {
            ExoPlayer
                .Builder(androidContext())
                .setAudioAttributes(get(), true)
                .setHandleAudioBecomingNoisy(true)
                .setTrackSelector(DefaultTrackSelector(androidContext()))
                .build()
        }
        singleOf(::PlaybackNotificationManager)
        singleOf(::PlaybackServiceHandler)
        single { CoroutineScope(SupervisorJob() + Dispatchers.Main) }
    }
