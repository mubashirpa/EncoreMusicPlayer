package com.encore.music.data.remote.dto.artists

import kotlinx.serialization.Serializable

@Serializable
data class ArtistsDto(
    val limit: Int? = null,
    val offset: Int? = null,
    val total: Int? = null,
    val items: List<Artist>? = null,
)
