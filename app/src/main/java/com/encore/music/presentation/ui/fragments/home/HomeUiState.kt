package com.encore.music.presentation.ui.fragments.home

import com.encore.music.core.UiText
import com.encore.music.domain.model.home.HomePlaylist

sealed class HomeUiState {
    data class Error(
        val message: UiText,
    ) : HomeUiState()

    data class Success(
        val playlists: List<HomePlaylist>,
    ) : HomeUiState()

    data object Empty : HomeUiState()

    data object Loading : HomeUiState()
}
