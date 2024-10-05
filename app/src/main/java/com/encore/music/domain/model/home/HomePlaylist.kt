package com.encore.music.domain.model.home

import com.encore.music.domain.model.playlists.Playlist

data class HomePlaylist(
    val title: String = "",
    val playlists: List<Playlist> = emptyList(),
)
