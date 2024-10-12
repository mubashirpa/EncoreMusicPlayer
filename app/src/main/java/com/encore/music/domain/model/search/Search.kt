package com.encore.music.domain.model.search

import com.encore.music.domain.model.artists.Artist
import com.encore.music.domain.model.playlists.Playlist
import com.encore.music.domain.model.tracks.Track

data class Search(
    val tracks: List<Track>? = null,
    val artists: List<Artist>? = null,
    val playlists: List<Playlist>? = null,
)
