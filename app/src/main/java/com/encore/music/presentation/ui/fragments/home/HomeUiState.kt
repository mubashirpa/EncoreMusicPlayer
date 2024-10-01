package com.encore.music.presentation.ui.fragments.home

sealed class HomeUiState {
    data object Error : HomeUiState()

    data object Loading : HomeUiState()

    data object Success : HomeUiState()

    data object Empty : HomeUiState()
}
