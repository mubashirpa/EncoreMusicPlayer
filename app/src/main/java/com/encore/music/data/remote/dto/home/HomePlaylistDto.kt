package com.encore.music.data.remote.dto.home

import com.encore.music.data.remote.dto.playlists.Playlist
import kotlinx.serialization.Serializable

@Serializable
data class HomePlaylistDto(
    val title: String = "",
    val playlists: List<Playlist> = emptyList(),
)
