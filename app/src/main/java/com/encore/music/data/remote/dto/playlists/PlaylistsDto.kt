package com.encore.music.data.remote.dto.playlists

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistsDto(
    val items: List<Playlist> = listOf(),
    val limit: Int = 0,
    val offset: Int = 0,
    val total: Int = 0,
)
