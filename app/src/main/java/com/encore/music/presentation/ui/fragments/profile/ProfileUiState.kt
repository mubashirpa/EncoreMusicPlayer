package com.encore.music.presentation.ui.fragments.profile

sealed class ProfileUiState {
    data object Logout : ProfileUiState()
}
