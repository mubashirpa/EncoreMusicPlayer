package com.encore.music.domain.model.search

import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track

sealed class SearchItem {
    data class TrackItem(
        val track: Track,
    ) : SearchItem()

    data class ArtistItem(
        val artist: Artist,
    ) : SearchItem()

    data class PlaylistItem(
        val playlist: Playlist,
    ) : SearchItem()
}
