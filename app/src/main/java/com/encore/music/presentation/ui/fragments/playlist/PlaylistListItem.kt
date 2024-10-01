package com.encore.music.presentation.ui.fragments.playlist

import com.encore.music.domain.model.spotify.playlists.Playlist
import com.encore.music.domain.model.spotify.tracks.Track

sealed class PlaylistListItem {
    data class HeaderItem(
        val playlist: Playlist,
    ) : PlaylistListItem()

    data class TracksItem(
        val track: Track,
    ) : PlaylistListItem()
}
