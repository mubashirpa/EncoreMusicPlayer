package com.encore.music.data.remote.dto.search

import com.encore.music.data.remote.dto.artists.Artist
import com.encore.music.data.remote.dto.playlists.Playlist
import com.encore.music.data.remote.dto.tracks.Track
import kotlinx.serialization.Serializable

@Serializable
data class SearchDto(
    val tracks: List<Track>? = null,
    val artists: List<Artist>? = null,
    val playlists: List<Playlist>? = null,
)
