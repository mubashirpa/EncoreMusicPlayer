package com.encore.music.domain.model.spotify.playlists

data class Playlist(
    val description: String? = null,
    val id: String = "",
    val imageUrl: String = "",
    val isPublic: Boolean? = null,
    val name: String = "",
    val ownerDisplayName: String? = null,
    val tracks: Int = 0,
)
