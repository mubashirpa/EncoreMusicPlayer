package com.encore.music.player

import com.encore.music.player.RepeatMode as PlayerRepeatMode

sealed class PlaybackState {
    data class Buffering(
        val progress: Long,
    ) : PlaybackState()

    data class CurrentPlaying(
        val mediaItemIndex: Int,
    ) : PlaybackState()

    data class Playing(
        val isPlaying: Boolean,
    ) : PlaybackState()

    data class Progress(
        val progress: Long,
    ) : PlaybackState()

    data class Ready(
        val duration: Long,
    ) : PlaybackState()

    data class RepeatMode(
        val repeatMode: PlayerRepeatMode,
    ) : PlaybackState()

    data class ShuffleMode(
        val isEnabled: Boolean,
    ) : PlaybackState()

    data class Volume(
        val volume: Float,
    ) : PlaybackState()

    data object Initial : PlaybackState()
}
