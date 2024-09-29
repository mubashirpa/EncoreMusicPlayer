package com.encore.music.player

sealed class PlayerEvent {
    data object PlayPause : PlayerEvent()

    data object SelectedAudioChange : PlayerEvent()

    data object Backward : PlayerEvent()

    data object SeekToNext : PlayerEvent()

    data object Forward : PlayerEvent()

    data object SeekTo : PlayerEvent()

    data object Stop : PlayerEvent()

    data class UpdateProgress(
        val newProgress: Float,
    ) : PlayerEvent()
}
