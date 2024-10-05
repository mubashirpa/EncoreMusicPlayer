package com.encore.music.presentation.ui.fragments.playlist

import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track

sealed class PlaylistListItem {
    data class HeaderItem(
        val playlist: Playlist,
    ) : PlaylistListItem()

    data class TracksItem(
        val track: Track,
    ) : PlaylistListItem()
}
