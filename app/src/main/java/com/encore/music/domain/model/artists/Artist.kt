package com.encore.music.domain.model.artists

import com.encore.music.domain.model.tracks.Track

data class Artist(
    val followers: Int? = null,
    val id: String? = null,
    val image: String? = null,
    val name: String? = null,
    val tracks: List<Track>? = null,
)
