package com.encore.music.presentation.ui.fragments.search

import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track

sealed class SearchListItem {
    data class ArtistsItem(
        val artists: List<Artist>,
    ) : SearchListItem()

    data class PlaylistsItem(
        val playlists: List<Playlist>,
    ) : SearchListItem()

    data class TracksItem(
        val tracks: List<Track>,
    ) : SearchListItem()
}
