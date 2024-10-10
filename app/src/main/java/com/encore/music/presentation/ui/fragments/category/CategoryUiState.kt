package com.encore.music.presentation.ui.fragments.category

import com.encore.music.core.UiText
import com.encore.music.domain.model.playlists.Playlist

sealed class CategoryUiState {
    data class Error(
        val message: UiText,
    ) : CategoryUiState()

    data class Success(
        val playlists: List<Playlist>,
    ) : CategoryUiState()

    data object Empty : CategoryUiState()

    data object Loading : CategoryUiState()
}
