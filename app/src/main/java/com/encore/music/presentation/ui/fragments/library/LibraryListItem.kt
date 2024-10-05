package com.encore.music.presentation.ui.fragments.library

import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track

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
