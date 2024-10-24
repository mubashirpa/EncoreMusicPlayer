package com.encore.music.presentation.ui.fragments.search

import com.encore.music.core.UiText

sealed class SearchUiState {
    data class Empty(
        val message: UiText,
    ) : SearchUiState()

    data class Error(
        val message: UiText,
    ) : SearchUiState()

    data class Success(
        val items: List<SearchListItem>,
    ) : SearchUiState()

    data object Loading : SearchUiState()
}
