package com.encore.music.presentation.ui.fragments.playlist

import com.encore.music.domain.model.playlists.Playlist

sealed class PlaylistUiEvent {
    data class OnCreatePlaylist(
        val playlist: Playlist,
    ) : PlaylistUiEvent()

    data class OnEditLocalPlaylist(
        val playlist: Playlist,
    ) : PlaylistUiEvent()

    data class OnInsertTrackToLocalPlaylist(
        val playlist: Playlist,
    ) : PlaylistUiEvent()

    data class OnRemoveTrackFromLocalPlaylist(
        val trackId: String,
    ) : PlaylistUiEvent()

    data object OnDeleteLocalPlaylist : PlaylistUiEvent()

    data object OnRetry : PlaylistUiEvent()

    data object OnSavePlaylist : PlaylistUiEvent()

    data object OnUnSavePlaylist : PlaylistUiEvent()
}
