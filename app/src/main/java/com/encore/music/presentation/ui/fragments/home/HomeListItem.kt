package com.encore.music.presentation.ui.fragments.home

import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track

sealed class HomeListItem {
    data class TopTracksItem(
        val tracks: List<Track>,
    ) : HomeListItem()

    data class PlaylistsItem(
        val title: String,
        val playlists: List<Playlist>,
    ) : HomeListItem()
}
