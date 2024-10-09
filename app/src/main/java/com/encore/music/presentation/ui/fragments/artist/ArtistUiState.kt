package com.encore.music.presentation.ui.fragments.artist

import com.encore.music.core.UiText
import com.encore.music.domain.model.artists.Artist

sealed class ArtistUiState {
    data class Error(
        val message: UiText,
    ) : ArtistUiState()

    data class Success(
        val artist: Artist,
        val isFollowed: Boolean,
    ) : ArtistUiState()

    data object Loading : ArtistUiState()
}
