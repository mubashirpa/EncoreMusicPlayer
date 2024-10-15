package com.encore.music.player

sealed class PlayerEvent {
    data class ChangeShuffleModeEnabled(
        val shuffleModeEnabled: Boolean,
    ) : PlayerEvent()

    data class UpdateProgress(
        val newProgress: Float,
    ) : PlayerEvent()

    data object Backward : PlayerEvent()

    data object Forward : PlayerEvent()

    data object PlayPause : PlayerEvent()

    data object SeekTo : PlayerEvent()

    data object SeekToNext : PlayerEvent()

    data object SeekToPrevious : PlayerEvent()

    data object SelectedAudioChange : PlayerEvent()

    data object Stop : PlayerEvent()
}
