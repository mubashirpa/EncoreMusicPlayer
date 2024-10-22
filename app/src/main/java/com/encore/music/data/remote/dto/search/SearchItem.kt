package com.encore.music.data.remote.dto.search

import com.encore.music.data.remote.dto.artists.Artist
import com.encore.music.data.remote.dto.playlists.Playlist
import com.encore.music.data.remote.dto.tracks.Track

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
