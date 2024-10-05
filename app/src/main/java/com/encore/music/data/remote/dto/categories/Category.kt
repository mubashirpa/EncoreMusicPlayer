package com.encore.music.data.remote.dto.categories

import com.encore.music.data.remote.dto.playlists.PlaylistsDto
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val icon: String? = null,
    val id: String? = null,
    val name: String? = null,
    val playlists: PlaylistsDto? = null,
)
