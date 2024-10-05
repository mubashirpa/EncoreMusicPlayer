package com.encore.music.data.remote.dto.tracks

import kotlinx.serialization.Serializable

@Serializable
data class TracksDto(
    val limit: Int? = null,
    val offset: Int? = null,
    val total: Int? = null,
    val items: List<Track>? = null,
)
