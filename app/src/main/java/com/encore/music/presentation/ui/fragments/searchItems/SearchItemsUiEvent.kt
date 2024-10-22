package com.encore.music.presentation.ui.fragments.searchItems

import com.encore.music.domain.model.playlists.Playlist

sealed class SearchItemsUiEvent {
    data class OnCreatePlaylist(
        val playlist: Playlist,
    ) : SearchItemsUiEvent()

    data class OnInsertTrackToLocalPlaylist(
        val playlist: Playlist,
    ) : SearchItemsUiEvent()
}
