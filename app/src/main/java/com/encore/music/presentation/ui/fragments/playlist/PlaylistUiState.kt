package com.encore.music.presentation.ui.fragments.playlist

import com.encore.music.core.UiText
import com.encore.music.domain.model.playlists.Playlist

sealed class PlaylistUiState {
    data class Error(
        val message: UiText,
    ) : PlaylistUiState()

    data class Success(
        val playlist: Playlist,
    ) : PlaylistUiState()

    data object Loading : PlaylistUiState()
}
