package com.encore.music.presentation.ui.fragments.playlist

import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track

sealed class PlaylistListItem(
    val id: String,
) {
    data class HeaderItem(
        val playlist: Playlist,
    ) : PlaylistListItem("header")

    data class TracksItem(
        val trackId: String,
        val track: Track,
    ) : PlaylistListItem(trackId)

    data object EmptyTracksItem : PlaylistListItem("empty")
}
