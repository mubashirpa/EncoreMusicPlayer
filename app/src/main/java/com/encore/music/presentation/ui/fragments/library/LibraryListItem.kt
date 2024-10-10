package com.encore.music.presentation.ui.fragments.library

import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track

sealed class LibraryListItem {
    data class ArtistsItem(
        val title: String,
        var artists: List<Artist>,
    ) : LibraryListItem()

    data class PlaylistsItem(
        val title: String,
        var playlists: List<Playlist>,
    ) : LibraryListItem()

    data class TracksItem(
        val title: String,
        var tracks: List<Track>,
    ) : LibraryListItem()
}
