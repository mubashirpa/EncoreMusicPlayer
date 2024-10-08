package com.encore.music.presentation.ui.fragments.playlist

import com.encore.music.domain.model.tracks.Track

sealed class PlaylistUiEvent {
    data class AddTrackToPlaylist(
        val track: Track,
    ) : PlaylistUiEvent()

    data object OnRetry : PlaylistUiEvent()

    data object SavePlaylist : PlaylistUiEvent()
}
