package com.encore.music.presentation.ui.fragments.library

import com.encore.music.domain.model.spotify.artists.Artist
import com.encore.music.domain.model.spotify.playlists.Playlist
import com.encore.music.domain.model.spotify.tracks.Track

sealed class LibraryListItem {
    data class ArtistsItem(
        val title: String,
        val artists: List<Artist>,
    ) : LibraryListItem()

    data class PlaylistsItem(
        val title: String,
        val playlists: List<Playlist>,
    ) : LibraryListItem()

    data class TracksItem(
        val title: String,
        val tracks: List<Track>,
    ) : LibraryListItem()
}
