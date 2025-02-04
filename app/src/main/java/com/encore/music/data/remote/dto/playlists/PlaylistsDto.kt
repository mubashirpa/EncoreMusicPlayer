package com.encore.music.data.remote.dto.playlists

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistsDto(
    val limit: Int? = null,
    val offset: Int? = null,
    val total: Int? = null,
    val items: List<Playlist>? = null,
)
