package com.encore.music.presentation.ui.fragments.library

sealed class LibraryUiState {
    data object Error : LibraryUiState()

    data object Loading : LibraryUiState()
}
