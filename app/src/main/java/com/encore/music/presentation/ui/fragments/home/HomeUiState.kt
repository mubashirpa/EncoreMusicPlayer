package com.encore.music.presentation.ui.fragments.home

import com.encore.music.domain.model.spotify.playlists.Playlist

data class HomeUiState(
    val popularPlaylists: List<Playlist> = emptyList(),
)
