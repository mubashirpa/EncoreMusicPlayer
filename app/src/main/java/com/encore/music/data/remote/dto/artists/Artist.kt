package com.encore.music.data.remote.dto.artists

import com.encore.music.data.remote.dto.tracks.Track
import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    val id: String? = null,
    val image: String? = null,
    val name: String? = null,
    val tracks: List<Track>? = null,
)
