package com.encore.music.player

sealed class PlaybackState {
    data object Initial : PlaybackState()

    data class Ready(
        val duration: Long,
    ) : PlaybackState()

    data class Progress(
        val progress: Long,
    ) : PlaybackState()

    data class Buffering(
        val progress: Long,
    ) : PlaybackState()

    data class Playing(
        val isPlaying: Boolean,
    ) : PlaybackState()

    data class CurrentPlaying(
        val mediaItemIndex: Int,
    ) : PlaybackState()
}
