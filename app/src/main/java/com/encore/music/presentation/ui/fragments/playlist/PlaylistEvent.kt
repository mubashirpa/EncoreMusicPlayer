package com.encore.music.presentation.ui.fragments.playlist

import com.encore.music.domain.model.playlists.Playlist

sealed class PlaylistEvent {
    data class OnCreatePlaylist(
        val playlist: Playlist,
    ) : PlaylistEvent()

    data class OnEditLocalPlaylist(
        val playlist: Playlist,
    ) : PlaylistEvent()

    data class OnInsertTrackToLocalPlaylist(
        val playlist: Playlist,
    ) : PlaylistEvent()

    data object OnDeleteLocalPlaylist : PlaylistEvent()

    data object OnRetry : PlaylistEvent()

    data object OnSavePlaylist : PlaylistEvent()

    data object OnUnSavePlaylist : PlaylistEvent()
}
