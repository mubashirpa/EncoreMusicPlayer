package com.encore.music.domain.model.playlists

import com.encore.music.domain.model.tracks.Track

data class Playlist(
    val description: String? = null,
    val id: String? = null,
    val image: String? = null,
    val name: String? = null,
    val owner: String? = null,
    val tracks: List<Track>? = null,
)
