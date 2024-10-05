package com.encore.music.data.remote.dto.tracks

import com.encore.music.data.remote.dto.artists.ArtistsDto
import kotlinx.serialization.Serializable

@Serializable
data class Track(
    val artists: ArtistsDto? = null,
    val id: String? = null,
    val image: String? = null,
    val name: String? = null,
    val mediaUrl: String? = null,
)
