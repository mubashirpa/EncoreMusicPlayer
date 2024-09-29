package com.encore.music.presentation.ui.fragments.home

import com.encore.music.domain.model.playlists.Playlist

sealed class HomeListItem {
    data class TopTracksItem(
        val tracks: List<Playlist>,
    ) : HomeListItem()

    data class PlaylistsItem(
        val title: String,
        val playlists: List<Playlist>,
    ) : HomeListItem()
}
