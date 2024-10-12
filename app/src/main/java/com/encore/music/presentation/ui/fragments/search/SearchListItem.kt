package com.encore.music.presentation.ui.fragments.search

import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track

sealed class SearchListItem(
    val id: String,
) {
    data class ArtistItem(
        val artist: Artist,
    ) : SearchListItem(artist.id.orEmpty())

    data class PlaylistItem(
        val playlist: Playlist,
    ) : SearchListItem(playlist.id.orEmpty())

    data class TrackItem(
        val track: Track,
    ) : SearchListItem(track.id.orEmpty())
}
