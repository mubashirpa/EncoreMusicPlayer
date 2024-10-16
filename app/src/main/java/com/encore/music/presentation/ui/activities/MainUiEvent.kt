package com.encore.music.presentation.ui.activities

import com.encore.music.domain.model.tracks.Track

sealed class MainUiEvent {
    data class AddNextInPlaylist(
        val track: Track,
    ) : MainUiEvent()

    data class AddPlaylist(
        val tracks: List<Track>,
        val selectedTrackId: String? = null,
    ) : MainUiEvent()

    data class AddToPlaylist(
        val track: Track,
    ) : MainUiEvent()

    data class ChangeShuffleModeEnabled(
        val shuffleModeEnabled: Boolean,
    ) : MainUiEvent()

    data class SeekTo(
        val position: Float,
    ) : MainUiEvent()

    data class SelectedAudioChange(
        val index: Int,
    ) : MainUiEvent()

    data class UpdateProgress(
        val newProgress: Float,
    ) : MainUiEvent()

    data object ChangeRepeatMode : MainUiEvent()

    data object PlayPause : MainUiEvent()

    data object SeekToNext : MainUiEvent()

    data object SeekToPrevious : MainUiEvent()
}
