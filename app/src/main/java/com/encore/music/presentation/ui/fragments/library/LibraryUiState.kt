package com.encore.music.presentation.ui.fragments.library

sealed class LibraryUiState {
    data object Empty : LibraryUiState()

    data object Loading : LibraryUiState()

    data object Success : LibraryUiState()
}
