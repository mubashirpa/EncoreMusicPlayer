package com.encore.music.data.remote.dto.playlists

import com.encore.music.data.remote.dto.tracks.TracksDto
import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val description: String? = null,
    val id: String? = null,
    val image: String? = null,
    val name: String? = null,
    val owner: String? = null,
    val tracks: TracksDto? = null,
)
