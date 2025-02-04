package com.encore.music.presentation.ui.fragments.playlist

import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track

sealed class PlaylistListItem(
    val id: String,
) {
    data class HeaderItem(
        val playlist: Playlist,
    ) : PlaylistListItem(playlist.id.orEmpty())

    data class TracksItem(
        val track: Track,
    ) : PlaylistListItem(track.id.orEmpty())

    data object EmptyTracksItem : PlaylistListItem("empty")
}
